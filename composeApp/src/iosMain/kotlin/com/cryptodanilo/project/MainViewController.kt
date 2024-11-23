package com.cryptodanilo.project

import androidx.compose.ui.window.ComposeUIViewController
import com.cryptodanilo.project.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }