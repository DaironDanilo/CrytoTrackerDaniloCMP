package com.cryptodanilo.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cryptodanilo.project.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CryptoTrackerDanilo",
        ) {
            App()
        }
    }
}