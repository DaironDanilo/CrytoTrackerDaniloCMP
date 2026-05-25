package com.cryptodanilo.project.crypto.presentation.coinlist.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSize(): IntSize =
    LocalWindowInfo.current
        .containerSize
