package com.cryptodanilo.project.core.presentation

// iPhone SE logical dimensions — the smallest mainstream phone screen size and the de facto
// minimum viewport for mobile-first layouts. Desktop (DesktopApp.kt) enforces this as the
// window's minimum size; web (WebMain.kt, styles.css) enforces it as the body's min-width/height
// so shrinking either surface below this never collapses the layout into something unusable.
object MinimumViewportSize {
    const val WIDTH_DP = 375
    const val HEIGHT_DP = 667
}
