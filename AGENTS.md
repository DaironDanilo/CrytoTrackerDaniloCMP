# CryptoTracker — Kotlin Multiplatform

KMP app (Android/iOS/Desktop/Web, Compose Multiplatform) backed by a Ktor
server. Full architecture/setup/run instructions: `README.md`. Backend
detail: `server/README.md`. This file is agent-operating rules and the
module map, kept in the open, cross-tool [AGENTS.md](https://agentskills.io)
format so any AI coding tool can read it, not just Claude Code — it does not
duplicate what those two READMEs already document.

## Module graph

```
:core (leaf, no deps)
 ├─ app:shared (implementation(projects.core))
 │   ├─ app:androidApp   ─┐
 │   ├─ app:desktopApp    ├─ implementation(projects.app.shared)
 │   ├─ app:webApp       ─┘
 │   └─ app:iosApp  (Xcode project, not a Gradle module — links app:shared's
 │                    compiled KMP framework via Xcode's build phase)
 └─ server (implementation(projects.core), NOT app:shared)
```

Implication when changing something: a change to `:core` can affect `:server`
*and* every client. A change to `app:shared` can affect all four client
targets but never `:server`. A change to one client app module
(`app:androidApp` etc.) affects only that target.

## Design tokens — never hardcode colors, spacing, or type

Full system documented in `app/shared/CLAUDE.md`. One-line version: use
`CryptoTrackerTheme.{colors,spacing,sizing,typography}` from
`app/shared/src/commonMain/kotlin/com/cryptodanilo/project/ui/theme/Theme.kt`
for every color/dp/sp/font in Compose code — never `Color(0x...)` or a bare
`.dp`/`.sp` literal. (Known pre-existing violation to *not* copy as a
pattern: `LineChart.kt:438-440`.) User-facing text goes through
`composeResources/values/strings.xml`, not inline string literals.

## Git / commit rules (standing — do not ask, just follow)

- **Never** add an AI tool as a co-author or contributor in any commit.
- **Never** push unless explicitly asked in that turn — committing is not
  the same as authorization to push.
- **Never** commit `BACKEND_ARCHITECTURE_PLAN.md` (already gitignored —
  don't remove that line).
- Prefer many small, focused commits over one large one when doing
  multi-step work, unless told otherwise.

## Deployment — no Cloud Build, ever

`:server` images are built with plain `docker build`/`docker push` (locally
or on a GitHub Actions runner), never `gcloud run deploy --source=` or
`gcloud builds submit`. This is a deliberate cost-control choice, not an
oversight — don't "helpfully" switch to Cloud Build.

## CI/CD (path-scoped, GitHub Actions)

Two independent trigger groups, both also triggered by root
build-infrastructure files (`gradle/libs.versions.toml`, root
`build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, the Gradle
wrapper) since a path filter can't tell which side a catalog/plugin bump
affects:

- **Backend**: `deploy-server.yml` (lint → test → build distroless image →
  deploy to Cloud Run → smoke-test the live URL) + `test-server.yml` (lint +
  test only, also on PR), both scoped to `server/**` + `core/**`.
- **Clients**: `build.yml` (all four targets build together as one unit —
  deliberately not further split, so a regression on any platform still
  fails CI even if that platform wasn't touched directly) + `test.yml`
  (lint + test), both scoped to `app/**` + `core/**`.

Editing a workflow file itself always triggers that workflow (each filter
includes its own `.github/workflows/*.yml` path).

`server/Dockerfile` is multi-stage/distroless
(`gcr.io/distroless/java17-debian12:nonroot`) and its build context is the
**repo root**, not `server/` — it needs `core/` and the root Gradle files to
configure the multi-module build.

## Verify before calling anything done

There is no single cross-module command (an Android/iOS/Desktop/Web/JVM
multi-target build doesn't have one target that "means everything passed"),
so verify at the module level that's actually relevant to the change:

- `./gradlew :server:check` — backend: ktlint + `:server:test` (Ktor
  `testApplication` + fake repositories, no live DB needed).
- `./gradlew :app:shared:ktlintCheck :app:shared:desktopTest` — shared/
  client logic (desktop target used as the fast JVM test runner for common
  code; this is what `test.yml` runs in CI).
- `./gradlew :<module>:ktlintCheck` for any other single module touched.
- `server/scripts/smoke-test.sh [base_url]` — real end-to-end check against
  a running `:server` (defaults to the live Cloud Run URL). Run this after
  any backend change that could affect request/response behavior, not just
  after a deploy — a passing unit test suite doesn't guarantee the real
  request path works.
- For UI changes: actually run the affected target (desktop is fastest to
  iterate on) and exercise the golden path — type-checking is not feature
  correctness.

A `PostToolUse` hook auto-runs `ktlintFormat` for the touched module after
every edit (Claude Code only — see `CLAUDE.md`), so most formatting issues
never reach the check step above at all.

## Project-shared memory

`memory/MEMORY.md` is a **git-committed, repo-shared** knowledge base of
non-obvious gotchas — distinct from any single AI tool's private session
memory, which only that tool/session will ever see again. Claude Code loads
its index automatically every session (imported from `CLAUDE.md`); other
tools should read it explicitly. Check it for known traps before debugging
something that feels like it's been hit before, and add an entry (same
frontmatter format as the existing ones) when you learn something the hard
way that a future agent — any agent, any tool, any session — would
otherwise re-discover from scratch.
