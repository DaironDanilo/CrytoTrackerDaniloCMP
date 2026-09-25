---
name: project_skiko_coil_version_warning
description: Benign "Skiko dependencies' versions are incompatible" warning from coil3 vs. Compose Multiplatform during desktop builds
metadata:
  type: project
---

Since bumping Compose Multiplatform to 1.12.1 (Skia Milestone 150, transitively
pulling `org.jetbrains.skiko:skiko:0.150.1`), desktop Gradle tasks
(`checkDesktopMainComposeLibrariesCompatibility`) print:

```
w: Skiko dependencies' versions are incompatible.
    io.coil-kt.coil3:coil-core-jvm:3.5.0
    \--- org.jetbrains.skiko:skiko:0.144.6 -> 0.150.1
```

**Why:** `coil3` 3.5.0 was built against an older skiko; Gradle's conflict
resolution wins with the newer skiko Compose Multiplatform 1.12.1 requires,
so the build still succeeds — this is a warning, not an error. Verified by
running `:app:desktopApp:run`: app launched, fetched live data from the
production API, rendered normally.

**How to apply:** Don't treat this warning as a regression to chase down
during dependency bumps. If it starts causing real runtime issues (crashes,
rendering glitches on desktop), check whether a newer `coil3` release aligns
its skiko version, and bump `coil3` in `gradle/libs.versions.toml`
accordingly rather than pinning skiko directly.
