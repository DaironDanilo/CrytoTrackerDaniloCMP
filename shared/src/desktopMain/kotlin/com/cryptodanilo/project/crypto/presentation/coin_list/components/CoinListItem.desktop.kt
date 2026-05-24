package com.cryptodanilo.project.crypto.presentation.coin_list.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSize(): IntSize {
    return LocalWindowInfo.current
        .containerSize
}
