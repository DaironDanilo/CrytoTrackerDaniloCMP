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
val generatePwaPrecacheManifest =
    tasks.register("generatePwaPrecacheManifest") {
        group = "distribution"
        description = "Generates precache-manifest.json for the PWA service worker from the wasmJs production distribution output."

        val distDir = layout.buildDirectory.dir("dist/wasmJs/productionExecutable")
        inputs.dir(distDir)
        outputs.file(distDir.map { it.file("precache-manifest.json") })

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

            val entries = files.joinToString(",\n") { file -> "    \"${file.relativeTo(dir).invariantSeparatorsPath}\"" }
            dir.resolve("precache-manifest.json").writeText(
                """
                {
                  "revision": "$revision",
                  "entries": [
                $entries
                  ]
                }
                """.trimIndent(),
            )
        }
    }

tasks.named("wasmJsBrowserDistribution") {
    finalizedBy(generatePwaPrecacheManifest)
}
