import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenExec

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
        val wasmJsMain by getting
        wasmJsMain.dependencies {
            implementation(projects.shared)
            implementation(libs.ui)
        }
    }
}

// wasm-opt version_125 segfaults on Linux CI runners with full optimization.
// -O0 keeps the production build pipeline intact while skipping the passes that crash.
tasks.withType<BinaryenExec>().configureEach {
    binaryenArguments = mutableListOf(
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
