---
description: Design token enforcement for any Compose Multiplatform code across all client app modules
paths:
  - "app/**/*.kt"
---

# Design tokens (applies to every client module, not just app:shared)

This rule exists because `app/shared/CLAUDE.md` only auto-loads when you're
reading files inside `app/shared/` itself — it won't surface if you're
editing Compose code from `app/androidApp/`, `app/desktopApp/`, or
`app/webApp/` (they depend on `app:shared` but are siblings, not
descendants, so its `CLAUDE.md` doesn't inherit to them). This path-scoped
rule closes that gap.

- Never hardcode a color: use `CryptoTrackerTheme.colors.*` (falls through
  to `MaterialTheme.colorScheme`), not `Color(0x......)`.
- Never hardcode spacing/sizing: use `CryptoTrackerTheme.spacing.*` /
  `CryptoTrackerTheme.sizing.*`, not a bare `.dp` literal.
- Never hardcode type: use `CryptoTrackerTheme.typography.*`, not a bare
  `.sp` literal or a one-off `TextStyle`.
- Never hardcode user-facing text: use `stringResource(Res.string.*)` from
  `composeResources/values/strings.xml`, not an inline string literal.

All of the above are defined in
`app/shared/src/commonMain/kotlin/com/cryptodanilo/project/ui/theme/`
(`Theme.kt`, `Color.kt`, `Spacing.kt`, `Sizing.kt`) — add a new token there
if nothing existing fits, rather than inlining a one-off value anywhere.

Known pre-existing violation, not a pattern to copy:
`app/shared/src/commonMain/kotlin/com/cryptodanilo/project/crypto/presentation/coinDetail/LineChart.kt:438-440`.
