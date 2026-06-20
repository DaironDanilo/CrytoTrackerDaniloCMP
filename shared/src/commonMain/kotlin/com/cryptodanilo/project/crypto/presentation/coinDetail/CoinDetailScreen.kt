package com.cryptodanilo.project.crypto.presentation.coinDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.crypto.presentation.coinDetail.components.InfoCard
import com.cryptodanilo.project.crypto.presentation.coinDetail.components.MarketsList
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListState
import com.cryptodanilo.project.crypto.presentation.coinList.components.conditional
import com.cryptodanilo.project.crypto.presentation.coinList.components.getScreenSize
import com.cryptodanilo.project.crypto.presentation.coinList.components.previewCoin
import com.cryptodanilo.project.crypto.presentation.models.toDisplayableNumber
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import com.cryptodanilo.project.ui.theme.greenBackground
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.change_last_24h
import cryptotrackerdanilo.shared.generated.resources.dollar
import cryptotrackerdanilo.shared.generated.resources.go_back
import cryptotrackerdanilo.shared.generated.resources.market_cap
import cryptotrackerdanilo.shared.generated.resources.price
import cryptotrackerdanilo.shared.generated.resources.stock
import cryptotrackerdanilo.shared.generated.resources.trending
import cryptotrackerdanilo.shared.generated.resources.trending_down
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
fun SharedTransitionScope.CoinDetailScreen(
    animatedPaneScope: AnimatedPaneScope? = null,
    state: CoinListState,
    shouldShowBackNavigationIcon: Boolean,
    shouldExistSharedElementTransition: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onAction: (CoinListAction) -> Unit = {},
) {
    val contentColor =
        if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else if (state.selectedCoinUi != null) {
        val coin = state.selectedCoinUi
        // Captured here, above the verticalScroll Column below, because a scrollable
        // Column measures its children with unbounded height — maxHeight read from
        // inside it would be infinite and useless for capping the chart's height.
        BoxWithConstraints {
            val availablePaneHeight = maxHeight
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(CryptoTrackerTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(coin.iconRes),
                    contentDescription = coin.name,
                    tint = CryptoTrackerTheme.colors.primary,
                    modifier =
                        Modifier
                            .size(CryptoTrackerTheme.sizing.coinDetailIconSize)
                            .conditional(
                                condition = shouldExistSharedElementTransition && animatedPaneScope != null,
                                ifTrue = {
                                    sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "image/${coin.id}"),
                                        animatedVisibilityScope = animatedPaneScope!!,
                                        boundsTransform = { _, _ ->
                                            tween(durationMillis = 1000)
                                        },
                                        renderInOverlayDuringTransition = false,
                                    )
                                },
                            ),
                )
                Text(
                    text = coin.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                )
                Text(
                    text = coin.symbol,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    InfoCard(
                        title = stringResource(Res.string.market_cap),
                        formattedValue = "$ ${coin.marketCapUsd.formatted}",
                        icon = Res.drawable.stock,
                    )
                    InfoCard(
                        title = stringResource(Res.string.price),
                        formattedValue = "$ ${coin.priceUsd.formatted}",
                        icon = Res.drawable.dollar,
                    )
                    val absoluteChangeFormatted =
                        ((coin.priceUsd.value) * (coin.changePercent24Hr.value / 100)).toDisplayableNumber()
                    val isPositiveChange = coin.changePercent24Hr.value > 0.0
                    val contentColorInfoCard =
                        if (isPositiveChange) {
                            if (isSystemInDarkTheme()) Color.Green else greenBackground
                        } else {
                            CryptoTrackerTheme.colors.error
                        }
                    InfoCard(
                        title = stringResource(Res.string.change_last_24h),
                        formattedValue = absoluteChangeFormatted.formatted,
                        icon =
                            if (isPositiveChange) {
                                Res.drawable.trending
                            } else {
                                Res.drawable.trending_down
                            },
                        contentColor = contentColorInfoCard,
                    )
                }

                // Chart | Markets tab switcher
                val tabShape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerSmall)
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = CryptoTrackerTheme.spacing.small)
                            .border(CryptoTrackerTheme.sizing.borderThin, CryptoTrackerTheme.colors.outline, tabShape)
                            .clip(tabShape),
                ) {
                    listOf(DetailTab.Chart, DetailTab.Markets).forEach { tab ->
                        val isSelected = state.selectedDetailTab == tab
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) CryptoTrackerTheme.colors.primary else Color.Transparent,
                                    ).clickable { onAction(CoinListAction.OnDetailTabSelected(tab)) }
                                    .padding(vertical = CryptoTrackerTheme.sizing.tabVerticalPadding),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = tab.name,
                                style = CryptoTrackerTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color =
                                    if (isSelected) {
                                        CryptoTrackerTheme.colors.onPrimary
                                    } else {
                                        CryptoTrackerTheme.colors.onSurface
                                    },
                            )
                        }
                    }
                }

                DetailTabContent(
                    state = state,
                    coinPriceHistory = coin.coinPriceHistory,
                    availablePaneHeight = availablePaneHeight,
                    onAction = onAction,
                )
            }
            if (shouldShowBackNavigationIcon) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.go_back),
                    tint = contentColor,
                    modifier =
                        Modifier
                            .clickable { onBack() }
                            .padding(CryptoTrackerTheme.spacing.large)
                            .size(CryptoTrackerTheme.sizing.backIconSize),
                )
            }
        }
    }
}

private val CHART_MIN_HEIGHT = 200.dp
private const val CHART_HEIGHT_FRACTION_OF_PANE = 0.6f

@Composable
private fun DetailTabContent(
    state: CoinListState,
    coinPriceHistory: List<DataPoint>,
    availablePaneHeight: Dp,
    onAction: (CoinListAction) -> Unit,
) {
    when (state.selectedDetailTab) {
        DetailTab.Chart -> {
            AnimatedVisibility(visible = coinPriceHistory.isNotEmpty()) {
                var selectedDataPoint by remember { mutableStateOf<DataPoint?>(null) }
                var labelWidth by remember { mutableFloatStateOf(0f) }
                var totalChartWidth by remember { mutableFloatStateOf(0f) }
                val amountOfVisibleDataPoints =
                    if (labelWidth > 0) {
                        ((totalChartWidth - 2.5 * labelWidth) / labelWidth).toInt()
                    } else {
                        0
                    }
                val startIndex =
                    (coinPriceHistory.lastIndex - amountOfVisibleDataPoints)
                        .coerceAtLeast(0)
                // Sized from a fraction of the real (un-scrolled) pane height captured
                // above, rather than from a fixed bucket, so a tall screen gets a taller
                // chart instead of leaving dead space below it. LineChart itself keeps
                // its grid rows roughly as tall as its columns are wide regardless of
                // the height it's given, so cells stay square at any size.
                val chartHeight =
                    (availablePaneHeight * CHART_HEIGHT_FRACTION_OF_PANE)
                        .coerceAtLeast(CHART_MIN_HEIGHT)
                LineChart(
                    dataPoints = coinPriceHistory,
                    style =
                        ChartStyle(
                            charLineColor = CryptoTrackerTheme.colors.primary,
                            unselectedColor = CryptoTrackerTheme.colors.secondary.copy(alpha = 0.3f),
                            selectedColor = CryptoTrackerTheme.colors.primary,
                            helperLinesThicknessPx = 5f,
                            axisLinesThicknessPx = 5f,
                            labelFontSize = 14.sp,
                            minYLabelSpacing = CryptoTrackerTheme.sizing.chartMinYLabelSpacing,
                            verticalPadding = CryptoTrackerTheme.spacing.small,
                            horizontalPadding = CryptoTrackerTheme.spacing.small,
                            xAxisLabelSpacing = CryptoTrackerTheme.spacing.small,
                        ),
                    visibleDataPointsIndices = startIndex..coinPriceHistory.lastIndex,
                    unit = "$",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(chartHeight)
                            .onSizeChanged { totalChartWidth = it.width.toFloat() },
                    selectedDataPoint = selectedDataPoint,
                    onSelectedDataPoint = { selectedDataPoint = it },
                    onXLabelWidthChange = { labelWidth = it },
                )
            }
        }

        DetailTab.Markets -> {
            val screenSize = getScreenSize()
            val marketsHeight = with(LocalDensity.current) { (screenSize.height * 0.85f).toDp() }
            MarketsList(
                state = state,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth().height(marketsHeight),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, name = "Light - With selected coin")
@Composable
private fun CoinDetailScreenLightPreview() {
    CryptoTrackerThemeProvider(darkTheme = false) {
        SharedTransitionLayout {
            CoinDetailScreen(
                state = CoinListState(selectedCoinUi = previewCoin),
                shouldShowBackNavigationIcon = true,
                shouldExistSharedElementTransition = false,
                onBack = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark - With selected coin")
@Composable
private fun CoinDetailScreenDarkPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        SharedTransitionLayout {
            CoinDetailScreen(
                state = CoinListState(selectedCoinUi = previewCoin),
                shouldShowBackNavigationIcon = false,
                shouldExistSharedElementTransition = false,
                onBack = {},
            )
        }
    }
}
