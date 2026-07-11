package com.cryptodanilo.project

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.cryptodanilo.project.core.presentation.MinimumViewportSize
import com.cryptodanilo.project.di.initKoin
import java.awt.Dimension

// Comfortably above MinimumViewportSize so the default window doesn't open at the smallest
// usable size.
private val defaultWindowWidth = (MinimumViewportSize.WIDTH_DP + 25).dp
private val defaultWindowHeight = (MinimumViewportSize.HEIGHT_DP + 33).dp

fun main() {
    initKoin()
    application {
        val state =
            rememberWindowState(
                width = defaultWindowWidth,
                height = defaultWindowHeight,
            )
        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = "CryptoTrackerDanilo",
        ) {
            // AWT's window.minimumSize is the actual mechanism Compose Desktop respects to stop
            // the user dragging the window smaller than MinimumViewportSize.
            LaunchedEffect(Unit) {
                window.minimumSize = Dimension(MinimumViewportSize.WIDTH_DP, MinimumViewportSize.HEIGHT_DP)
            }
            App()
        }
    }
}
