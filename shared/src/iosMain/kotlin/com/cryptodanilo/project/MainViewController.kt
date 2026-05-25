package com.cryptodanilo.project

import androidx.compose.ui.window.ComposeUIViewController
import com.cryptodanilo.project.di.initKoin

@Suppress("FunctionName")
fun MainViewController() =
    ComposeUIViewController(
        configure = {
            initKoin()
        },
    ) { App() }
