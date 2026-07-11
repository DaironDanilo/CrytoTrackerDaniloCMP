package com.cryptodanilo.project

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cryptodanilo.project.core.presentation.MinimumViewportSize
import com.cryptodanilo.project.di.initKoin
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalComposeUiApi::class, ExperimentalWasmJsInterop::class)
fun main() {
    initKoin()

    // Mirrored in styles.css so the constraint holds even before this script runs.
    val body = document.body
    if (body != null) {
        body.style.minWidth = "${MinimumViewportSize.WIDTH_DP}px"
        body.style.minHeight = "${MinimumViewportSize.HEIGHT_DP}px"
    }

    // Bridges the app's in-app back-navigable state (list <-> detail pane) onto the
    // browser's history stack, so the Android system back button (which Chrome/PWA
    // maps to browser history navigation) goes back in-app instead of closing the PWA.
    //
    // We push exactly one history entry while a "back-navigable" destination (the detail
    // pane) is showing, and pop it again once it's gone - whether that happens via the
    // browser's own back button or via in-app UI (e.g. tapping the on-screen back icon).
    val backRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    var isDetailEntryPushed = false
    var ignoreNextPopState = false

    window.addEventListener("popstate") {
        if (ignoreNextPopState) {
            ignoreNextPopState = false
            return@addEventListener
        }
        if (isDetailEntryPushed) {
            isDetailEntryPushed = false
            backRequests.tryEmit(Unit)
        }
    }

    ComposeViewport(document.body!!) {
        App(
            onBackNavigableChanged = { canNavigateBack ->
                if (canNavigateBack && !isDetailEntryPushed) {
                    isDetailEntryPushed = true
                    window.history.pushState(null, "")
                } else if (!canNavigateBack && isDetailEntryPushed) {
                    isDetailEntryPushed = false
                    ignoreNextPopState = true
                    window.history.back()
                }
            },
            backRequests = backRequests,
        )
    }
}
