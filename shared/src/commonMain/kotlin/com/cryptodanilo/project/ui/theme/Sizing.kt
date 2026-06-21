package com.cryptodanilo.project.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Sizing(
    // Icon sizes
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 40.dp,
    val iconExtraLarge: Dp = 64.dp,
    val coinIconSize: Dp = 48.dp,
    val coinIconListSize: Dp = 85.dp,
    val coinDetailIconSize: Dp = 80.dp,
    val priceChangeIconSize: Dp = 20.dp,
    val infoCardIconSize: Dp = 48.dp,
    val backIconSize: Dp = 36.dp,
    val exchangeChipSize: Dp = 28.dp,
    // Borders
    val borderThin: Dp = 1.dp,
    val borderMedium: Dp = 2.dp,
    // Corner radii
    val cornerSmall: Dp = 4.dp,
    val cornerMedium: Dp = 8.dp,
    val cornerLarge: Dp = 16.dp,
    val cornerFull: Dp = 50.dp,
    // Chart dimensions
    val chartHeight: Dp = 200.dp,
    val chartMinYLabelSpacing: Dp = 25.dp,
    // Component-specific dimensions
    val cardElevation: Dp = 15.dp,
    val infoCardInnerPadding: Dp = 12.dp,
    val tabVerticalPadding: Dp = 12.dp,
    val errorRetrySpacing: Dp = 12.dp,
    val marketItemSubRowSpacing: Dp = 2.dp,
    val marketItemInnerPadding: Dp = 12.dp,
    val rankColumnWidth: Dp = 32.dp,
    val rankExchangeSpacing: Dp = 12.dp,
    val marketsListCompactBreakpoint: Dp = 600.dp,
    val statusDotSize: Dp = 8.dp,
)

val LocalSizing = staticCompositionLocalOf { Sizing() }
