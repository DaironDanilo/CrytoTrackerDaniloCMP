import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ktlint) apply false
}

// Ktor's wasmJs client engine and Coil's coil-network-ktor3 each bundle their own exact `ws`
// npm pin (8.18.0 / 8.20.1) predating GHSA fixes for that package, so kotlin-js-store/wasm/yarn.lock
// keeps re-resolving to a vulnerable version no matter how often it's regenerated. This forces
// every wasmJs klib's `ws` dependency to a single patched version instead of waiting on upstream.
rootProject.plugins.withType(WasmYarnPlugin::class.java) {
    rootProject.the<WasmYarnRootExtension>().resolution("**/ws", "8.21.1")
}

subprojects {
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        filter {
            exclude { it.file.path.contains("/generated/") }
        }
    }

    afterEvaluate {
        tasks.matching { it.name.startsWith("runKtlintCheckOver") || it.name.startsWith("runKtlintFormatOver") }
            .configureEach {
                (this as? SourceTask)
                    ?.exclude { element -> element.file.path.contains("/generated/") }
            }
    }
}