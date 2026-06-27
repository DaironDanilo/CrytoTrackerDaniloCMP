package com.cryptodanilo.project.crypto.presentation.coinDetail

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptodanilo.project.core.presentation.components.LastUpdatedRow
import com.cryptodanilo.project.core.presentation.util.formatAbbreviatedPrice
import com.cryptodanilo.project.core.presentation.util.formatPriceChange
import com.cryptodanilo.project.crypto.presentation.coinDetail.components.ChartTimeframeSelector
import com.cryptodanilo.project.crypto.presentation.coinDetail.components.InfoCard
import com.cryptodanilo.project.crypto.presentation.coinDetail.components.MarketsList
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListState
import com.cryptodanilo.project.crypto.presentation.coinList.components.conditional
import com.cryptodanilo.project.crypto.presentation.coinList.components.previewCoin
import com.cryptodanilo.project.crypto.presentation.models.CoinUi
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
        Box {
            // The chart (and Markets list) normally take exactly the space left over
            // after the header/cards/tabs, with no scrolling — true whether this pane
            // is the only one showing (mobile) or sits next to the coin list
            // (desktop/web dual-pane). But that remaining space can shrink to nothing
            // on a very short window, so it's clamped to CHART_MIN_HEIGHT and the
            // screen falls back to scrolling only when that minimum doesn't fit.
            BoxWithConstraints(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(CryptoTrackerTheme.spacing.medium),
            ) {
                val density = LocalDensity.current
                var headerHeightPx by remember { mutableFloatStateOf(0f) }
                val headerHeightDp = with(density) { headerHeightPx.toDp() }
                val remainingSpaceHeight = (maxHeight - headerHeightDp).coerceAtLeast(CHART_MIN_HEIGHT)

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().onSizeChanged { headerHeightPx = it.height.toFloat() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CoinDetailHeaderAndTabs(
                            coin = coin,
                            state = state,
                            contentColor = contentColor,
                            shouldExistSharedElementTransition = shouldExistSharedElementTransition,
                            animatedPaneScope = animatedPaneScope,
                            onAction = onAction,
                        )
                    }
                    DetailTabContent(
                        state = state,
                        coinPriceHistory = coin.coinPriceHistory,
                        remainingSpaceModifier = Modifier.height(remainingSpaceHeight),
                        onAction = onAction,
                    )
                }
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

@Composable
private fun SharedTransitionScope.CoinDetailHeaderAndTabs(
    coin: CoinUi,
    state: CoinListState,
    contentColor: Color,
    shouldExistSharedElementTransition: Boolean,
    animatedPaneScope: AnimatedPaneScope?,
    onAction: (CoinListAction) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = coin.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = contentColor,
        )
        Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.small))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
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
            Spacer(modifier = Modifier.width(CryptoTrackerTheme.spacing.small))
            Text(
                text = coin.symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                color = contentColor,
            )
        }
    }
    Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.medium))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        InfoCard(
            title = stringResource(Res.string.market_cap),
            formattedValue = "$ ${coin.marketCapUsd.value.formatAbbreviatedPrice()}",
            icon = Res.drawable.stock,
        )
        InfoCard(
            title = stringResource(Res.string.price),
            formattedValue = "$ ${coin.priceUsd.value.formatAbbreviatedPrice()}",
            icon = Res.drawable.dollar,
        )
        val absoluteChangeValue = (coin.priceUsd.value) * (coin.changePercent24Hr.value / 100)
        val isPositiveChange = coin.changePercent24Hr.value > 0.0
        val contentColorInfoCard =
            if (isPositiveChange) {
                if (isSystemInDarkTheme()) Color.Green else greenBackground
            } else {
                CryptoTrackerTheme.colors.error
            }
        InfoCard(
            title = stringResource(Res.string.change_last_24h),
            formattedValue = absoluteChangeValue.formatPriceChange(),
            icon =
                if (isPositiveChange) {
                    Res.drawable.trending
                } else {
                    Res.drawable.trending_down
                },
            contentColor = contentColorInfoCard,
            accentColor = if (isPositiveChange) CryptoTrackerTheme.colors.primary else CryptoTrackerTheme.colors.error,
        )
    }
    Spacer(modifier = Modifier.size(CryptoTrackerTheme.spacing.medium))
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
}

private val CHART_MIN_HEIGHT = 200.dp

@Composable
private fun DetailTabContent(
    state: CoinListState,
    coinPriceHistory: List<DataPoint>,
    onAction: (CoinListAction) -> Unit,
    // Modifier.weight(1f) from the non-scrollable parent Column, applied to whichever
    // tab is currently active so it exactly fills the space left after the
    // header/cards/tabs — no scrolling needed, on either pane layout.
    remainingSpaceModifier: Modifier = Modifier,
) {
    when (state.selectedDetailTab) {
        DetailTab.Chart -> {
            Column(modifier = remainingSpaceModifier) {
                // Range chips (horizontally scrollable) + refresh button pinned to the right.
                // Both apply to the currently-selected range only.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ChartTimeframeSelector(
                        selectedTimeframe = state.selectedTimeframe,
                        onTimeframeSelected = { onAction(CoinListAction.OnTimeframeSelected(it)) },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(CryptoTrackerTheme.spacing.small))
                    val buttonShape = RoundedCornerShape(CryptoTrackerTheme.sizing.cornerMedium)
                    Box(
                        modifier =
                            Modifier
                                .size(CryptoTrackerTheme.sizing.iconLarge)
                                .background(
                                    color = CryptoTrackerTheme.colors.surfaceContainerHighest,
                                    shape = buttonShape,
                                ).border(
                                    width = CryptoTrackerTheme.sizing.borderThin,
                                    color = CryptoTrackerTheme.colors.primary.copy(alpha = 0.35f),
                                    shape = buttonShape,
                                ).clickable(enabled = !state.isManualRefreshingDetail) {
                                    onAction(CoinListAction.OnDetailManualRefresh)
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.isManualRefreshingDetail) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(CryptoTrackerTheme.sizing.iconSmall),
                                color = CryptoTrackerTheme.colors.primary,
                                strokeWidth = CryptoTrackerTheme.sizing.borderThin,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = CryptoTrackerTheme.colors.primary,
                                modifier = Modifier.size(CryptoTrackerTheme.sizing.iconMedium),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.small))
                // Freshness badge — right-aligned caption directly above the chart.
                // Shown only once a range has been fetched; reflects the selected range's timestamp.
                state.lastUpdatedDetailMs?.let { updatedAt ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        LastUpdatedRow(
                            updatedAt = updatedAt,
                            isLoading = state.isManualRefreshingDetail,
                            onRefresh = { onAction(CoinListAction.OnDetailManualRefresh) },
                            showRefreshButton = false,
                            modifier =
                                Modifier.padding(
                                    horizontal = CryptoTrackerTheme.spacing.small,
                                    vertical = CryptoTrackerTheme.spacing.extraSmall,
                                ),
                        )
                    }
                }
                // Same condition as the "Change last 24h" card: positive when > 0, flat/down otherwise.
                // This keeps the chart color and the card color in guaranteed sync.
                val isPositiveChange = (state.selectedCoinUi?.changePercent24Hr?.value ?: 0.0) > 0.0
                val chartLineColor =
                    if (isPositiveChange) {
                        CryptoTrackerTheme.colors.primary
                    } else {
                        CryptoTrackerTheme.colors.error
                    }
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = coinPriceHistory.isNotEmpty(),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        var selectedDataPoint by remember { mutableStateOf<DataPoint?>(null) }
                        // Full, untouched range and labels are always passed through —
                        // LineChart caps how many x-axis labels it draws internally, and
                        // the selected point's tooltip needs its real label intact.
                        LineChart(
                            dataPoints = coinPriceHistory,
                            style =
                                ChartStyle(
                                    charLineColor = chartLineColor,
                                    unselectedColor = CryptoTrackerTheme.colors.secondary.copy(alpha = 0.3f),
                                    selectedColor = chartLineColor,
                                    helperLinesThicknessPx = 5f,
                                    axisLinesThicknessPx = 5f,
                                    labelFontSize = 14.sp,
                                    minYLabelSpacing = CryptoTrackerTheme.sizing.chartMinYLabelSpacing,
                                    verticalPadding = CryptoTrackerTheme.spacing.small,
                                    horizontalPadding = CryptoTrackerTheme.spacing.small,
                                    xAxisLabelSpacing = CryptoTrackerTheme.spacing.small,
                                ),
                            visibleDataPointsIndices = coinPriceHistory.indices,
                            unit = "$",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .heightIn(min = CHART_MIN_HEIGHT),
                            selectedDataPoint = selectedDataPoint,
                            onSelectedDataPoint = { selectedDataPoint = it },
                        )
                    }
                    if (state.isLoadingCoinHistory) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }

        DetailTab.Markets -> {
            // MarketsList is backed by a LazyColumn that scrolls its own content
            // internally, so handing it the same weight(1f) share as the chart doesn't
            // change anything about its content — it just fills the remaining space.
            MarketsList(
                state = state,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth().then(remainingSpaceModifier),
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
