import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenExec
import java.security.MessageDigest

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "webApp"
        browser {
            commonWebpackConfig {
                outputFileName = "webApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val wasmJsMain = getByName("wasmJsMain")
        wasmJsMain.dependencies {
            implementation(projects.shared)
            implementation(libs.ui)
        }
    }
}

// wasm-opt version_125 segfaults on Linux CI runners with full optimization.
// -O0 keeps the production build pipeline intact while skipping the passes that crash.
tasks.withType<BinaryenExec>().configureEach {
    binaryenArguments =
        mutableListOf(
            "--enable-reference-types",
            "--enable-gc",
            "--enable-bulk-memory",
            "--enable-exception-handling",
            "--enable-nontrapping-float-to-int",
            "--enable-sign-ext",
            "--enable-multivalue",
            "-O0",
        )
}

// The production webpack bundle's JS/Wasm chunk filenames are content-hashed (e.g.
// 67f32c81739db0b4231f.wasm) and shift on every rebuild, so the service worker can't
// precache a hardcoded file list. This task scans the actual distribution output after
// it's built and writes that file list - plus one content hash covering all of it, used
// as the cache name - so service-worker.js always precaches exactly what got shipped.
//
// Entries are split into "critical" (the app shell: HTML/JS/Wasm/manifest/favicons -
// everything needed to boot the app) and "bulk" (the ~700 individual coin-icon resources
// under composeResources/). The service worker awaits "critical" before it awaits "bulk",
// so the app is guaranteed bootable offline even if it goes offline mid-install before the
// much larger icon set finishes caching.
//
// The computed revision is also substituted into service-worker.js's BUILD_REVISION
// placeholder, so the cache name (and the service worker's own file content) changes on
// every deploy - which is what makes the browser notice the new worker and update it.
val generatePwaPrecacheManifest =
    tasks.register("generatePwaPrecacheManifest") {
        group = "distribution"
        description = "Generates precache-manifest.json and bakes the build revision into service-worker.js."

        val distDir = layout.buildDirectory.dir("dist/wasmJs/productionExecutable")
        inputs.dir(distDir)
        outputs.file(distDir.map { it.file("precache-manifest.json") })
        outputs.file(distDir.map { it.file("service-worker.js") })

        doLast {
            val dir = distDir.get().asFile
            if (!dir.exists()) return@doLast

            val excludedNames = setOf("precache-manifest.json", "service-worker.js", "_headers")
            val excludedExtensions = setOf("map", "txt")

            val files =
                dir
                    .walkTopDown()
                    .filter { file -> file.isFile }
                    .filterNot { file -> file.name in excludedNames }
                    .filterNot { file -> file.extension in excludedExtensions }
                    .sortedBy { file -> file.relativeTo(dir).invariantSeparatorsPath }
                    .toList()

            val digest = MessageDigest.getInstance("MD5")
            files.forEach { file -> digest.update(file.readBytes()) }
            val revision = digest.digest().joinToString("") { byte -> "%02x".format(byte) }

            val (bulk, critical) = files.partition { file -> file.relativeTo(dir).invariantSeparatorsPath.startsWith("composeResources/") }

            // The .wasm bundles are webpack content-hashed (confirmed [immutable] in the
            // webpack build log) and weigh ~18MB - the page's own boot already downloads
            // them, so re-fetching with cache:'no-store' here would silently double that
            // transfer. Every other critical entry has a fixed name that doesn't change
            // when its content does, so those must bypass the cache to avoid going stale.
            fun List<File>.toJsonArray() =
                joinToString(",\n") { file ->
                    val path = file.relativeTo(dir).invariantSeparatorsPath
                    val immutable = file.extension == "wasm"
                    "    { \"url\": \"$path\", \"immutable\": $immutable }"
                }

            dir.resolve("precache-manifest.json").writeText(
                """
                {
                  "revision": "$revision",
                  "critical": [
                ${critical.toJsonArray()}
                  ],
                  "bulk": [
                ${bulk.toJsonArray()}
                  ]
                }
                """.trimIndent(),
            )

            val serviceWorkerFile = dir.resolve("service-worker.js")
            serviceWorkerFile.writeText(
                serviceWorkerFile.readText().replace("__BUILD_REVISION__", revision),
            )
        }
    }

tasks.named("wasmJsBrowserDistribution") {
    finalizedBy(generatePwaPrecacheManifest)
}
