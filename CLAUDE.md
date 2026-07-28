@AGENTS.md
@memory/MEMORY.md

Everything from `AGENTS.md` is shared across AI coding tools (the open
`AGENTS.md` standard — Codex, Cursor, Copilot, Gemini CLI, etc. all read it
natively; Claude Code doesn't, hence this import) and kept there so it
stays portable. `memory/MEMORY.md` (the index only — a couple of lines;
individual gotcha entries stay on-demand, loaded when actually relevant,
same as Claude Code's own native auto-memory pattern) is imported here so
its index is guaranteed loaded every session rather than relying on the
prose instruction below being followed. Everything else below is
Claude-Code-specific mechanism, not duplicated content.

## Nested CLAUDE.md files

Nested `CLAUDE.md` files exist per module (`core/`, `server/`,
`app/shared/`) and load automatically the moment you read a file in that
directory — you don't need to open them manually.

## Skills

`.claude/skills/new-compose-screen` — `/new-compose-screen` scaffolds a new
screen matching this repo's established ViewModel/DI/nav pattern (see
`app/shared/CLAUDE.md` for the pattern itself).

## Subagents

`.claude/agents/compose-ui-reviewer` — read-only review of Compose UI
changes against the design-token/string-resource/preview conventions.

## Rules

`.claude/rules/design-tokens.md` — path-scoped (`app/**/*.kt`)
reinforcement of the design-token rule, covering the three client shells
that don't inherit `app/shared/CLAUDE.md` (nested `CLAUDE.md` only loads for
descendants of the directory it's in, not siblings).

## Hooks

`.claude/settings.json`'s `PostToolUse` hook runs
`.claude/hooks/ktlint-format.sh` after every `Edit`/`Write` on a `.kt` file
— auto-formats via the touched file's module-scoped `ktlintFormat` Gradle
task, so formatting doesn't depend on remembering to run it.

## Permissions

`.claude/settings.json` also encodes two of the standing rules from
`AGENTS.md` as actual permission gates, not just prose: `git push` requires
explicit confirmation (`ask`), and `gcloud run deploy --source=`/
`gcloud builds submit` are denied outright (the "no Cloud Build" rule).
