#!/usr/bin/env bash
# PostToolUse hook (Edit|Write): auto-format the edited Kotlin file's module
# via ktlint immediately, instead of relying on an agent to remember to run
# it -- formatting becomes mechanical rather than something that depends on
# being told. No standalone ktlint CLI is installed on this project, so this
# goes through the module-scoped Gradle task rather than per-file (slower,
# but works out of the box). Always exits 0: a lint hook should never block
# the edit that triggered it, only surface anything it couldn't auto-fix.
set -uo pipefail

file_path=$(jq -r '.tool_input.file_path // empty' 2>/dev/null)
[[ "$file_path" == *.kt ]] || exit 0

case "$file_path" in
  */core/*) module=":core" ;;
  */server/*) module=":server" ;;
  */app/shared/*) module=":app:shared" ;;
  */app/androidApp/*) module=":app:androidApp" ;;
  */app/desktopApp/*) module=":app:desktopApp" ;;
  */app/webApp/*) module=":app:webApp" ;;
  *) exit 0 ;;
esac

./gradlew "${module}:ktlintFormat" -q --console=plain 2>&1 | tail -20
exit 0
