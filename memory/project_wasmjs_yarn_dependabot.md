---
name: wasmjs-yarn-dependabot-alerts
description: How to investigate and fix Dependabot alerts on kotlin-js-store yarn.lock files in this KMP project
metadata:
  type: project
---

Dependabot alerts against `kotlin-js-store/**/yarn.lock` in this repo have two distinct root causes — check which one applies before trying to "fix" a package version directly.

**1. A stale, unused lockfile.** This project only builds a `wasmJs` target (see `app/webApp/build.gradle.kts` — no plain `js()` target exists). The wasmJs Yarn/Node tooling stores its lockfile at `kotlin-js-store/wasm/yarn.lock`, and only `kotlinWasm*` Gradle tasks exist (`./gradlew tasks --all | grep -i yarn`) — there is no plain `kotlinUpgradeYarnLock` task. If a root-level `kotlin-js-store/yarn.lock` exists (no `wasm/` in the path) and predates the current setup, it's dead weight from an old `js(IR) { browser {} }` target that no longer exists — nothing regenerates or reads it. Confirm via `git log -1 -- kotlin-js-store/yarn.lock` (stale date) and `grep -rn "kotlin-js-store/yarn.lock" .` (no references). Fix: just delete it. This alone closed 72 of 74 open alerts in July 2026.

**2. A transitive npm dependency pinned by an upstream KMP library.** The real (non-stale) `kotlin-js-store/wasm/yarn.lock` picks up npm deps bundled by Kotlin/Wasm klibs — e.g. Ktor's wasmJs client engine and Coil's `coil-network-ktor3` each bundle their own exact `ws` pin. Regenerating the lockfile (`./gradlew kotlinWasmUpgradeYarnLock`) does **not** fix this: each klib's own `package.json` re-requests its exact pinned (vulnerable) version every time, so re-resolution just reselects the higher of the vulnerable pins.

**Fix**: force a yarn selective-resolution in the root `build.gradle.kts`:

```kotlin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension

rootProject.plugins.withType(WasmYarnPlugin::class.java) {
    rootProject.the<WasmYarnRootExtension>().resolution("**/ws", "8.21.1")
}
```

**Why the `"**/ws"` glob matters**: `resolution("ws", version)` (bare package name, no glob) silently does nothing — it only overrides a *direct* dependency of the root `package.json` named exactly `ws`, and here `ws` only ever appears as a *transitive* dependency nested inside other packages' manifests. Yarn classic's `resolutions` field needs the `**/` prefix to match at any depth. This distinction cost real debugging time (had to decompile `kotlin-gradle-plugin-2.4.10.jar`'s `BaseYarnRootExtension.resolution(String, String)` to find the right call shape) because the wrong form fails silently with no error — same Gradle output, same "BUILD SUCCESSFUL", lockfile just doesn't change.

**Also non-obvious**: after adding/changing a `resolution(...)` call, `./gradlew kotlinWasmUpgradeYarnLock` alone can report `UP-TO-DATE` and skip re-resolving even though the build script changed. Force it with `--rerun-tasks` (and `--no-configuration-cache` to be safe) the first time to confirm the override actually took effect in `kotlin-js-store/wasm/yarn.lock` before trusting a plain re-run.

**How to apply:** Any time GitHub reports new Dependabot alerts on a `kotlin-js-store` lockfile, first check whether the flagged manifest path is the stale root file (delete it) vs. the live `wasm/` one (add/extend a `resolution(...)` override, then verify with `:app:shared:wasmJsTest` and `:app:webApp:wasmJsBrowserDistribution` since `ws` here is dev/test-transport-only, not part of the shipped browser bundle).
