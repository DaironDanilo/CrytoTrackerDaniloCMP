package com.cryptodanilo.project.crypto.presentation.coin_list.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize

@Composable
actual fun getScreenSize(): IntSize {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    return IntSize(screenWidthDp, screenHeightDp)
}
