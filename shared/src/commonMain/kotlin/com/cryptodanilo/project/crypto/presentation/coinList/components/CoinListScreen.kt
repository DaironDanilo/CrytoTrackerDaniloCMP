package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.core.presentation.components.LastUpdatedRow
import com.cryptodanilo.project.core.presentation.components.ShimmerOverlay
import com.cryptodanilo.project.core.presentation.util.PullToRefreshWrapper
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListState
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.ASSET_COLUMN_WEIGHT
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.CHANGE_COLUMN_WEIGHT
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.PRICE_COLUMN_WEIGHT
import com.cryptodanilo.project.crypto.presentation.coinList.components.CoinListConstants.TREND_COLUMN_WEIGHT
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.coin_list_error
import cryptotrackerdanilo.shared.generated.resources.coin_list_header_asset
import cryptotrackerdanilo.shared.generated.resources.coin_list_header_change_pct
import cryptotrackerdanilo.shared.generated.resources.coin_list_header_price
import cryptotrackerdanilo.shared.generated.resources.coin_list_header_trend
import cryptotrackerdanilo.shared.generated.resources.coins_all_loaded
import cryptotrackerdanilo.shared.generated.resources.load_more
import cryptotrackerdanilo.shared.generated.resources.retry
import cryptotrackerdanilo.shared.generated.resources.search_no_results_hint
import cryptotrackerdanilo.shared.generated.resources.search_no_results_title
import cryptotrackerdanilo.shared.generated.resources.search_results_count
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CoinListScreen(
    animatedPaneScope: AnimatedPaneScope? = null,
    state: CoinListState,
    shouldExistSharedElementTransition: Boolean,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier,
    isSearchBarFocusable: Boolean = true,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        state.isError -> {
            Column(
                modifier = modifier.fillMaxSize().padding(CryptoTrackerTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.coin_list_error),
                    style = CryptoTrackerTheme.typography.bodyMedium,
                    color = CryptoTrackerTheme.colors.error,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(CryptoTrackerTheme.sizing.errorRetrySpacing))
                Button(onClick = { onAction(CoinListAction.OnRefresh) }) {
                    Text(stringResource(Res.string.retry))
                }
            }
        }

        else -> {
            val displayedCoins =
                if (state.searchQuery.isBlank()) {
                    state.coins
                } else {
                    state.coins.filter { coin ->
                        coin.symbol.contains(state.searchQuery, ignoreCase = true) ||
                            coin.name.contains(state.searchQuery, ignoreCase = true)
                    }
                }

            Column(modifier = modifier.fillMaxSize()) {
                CoinSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onAction(CoinListAction.OnSearchQueryChange(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    isFocusable = isSearchBarFocusable,
                )
                state.lastUpdatedMs?.let { updatedAt ->
                    LastUpdatedRow(
                        updatedAt = updatedAt,
                        isLoading = state.isManualRefreshing,
                        onRefresh = { onAction(CoinListAction.OnManualRefresh) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = CryptoTrackerTheme.spacing.medium,
                                    vertical = CryptoTrackerTheme.spacing.extraSmall,
                                ),
                    )
                }

                val isSearching = state.searchQuery.isNotBlank()
                val hasNoResults = isSearching && displayedCoins.isEmpty()

                if (isSearching && !hasNoResults) {
                    Text(
                        text = stringResource(Res.string.search_results_count, displayedCoins.size, state.searchQuery),
                        style = CryptoTrackerTheme.typography.bodySmall,
                        color = CryptoTrackerTheme.colors.onSurfaceVariant,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = CryptoTrackerTheme.spacing.medium,
                                    vertical = CryptoTrackerTheme.spacing.extraSmall,
                                ),
                    )
                }

                CoinListHeader(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = CryptoTrackerTheme.spacing.medium),
                )

                PullToRefreshWrapper(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onAction(CoinListAction.OnRefresh) },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    if (hasNoResults) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = stringResource(Res.string.search_no_results_title),
                                style = CryptoTrackerTheme.typography.headlineMedium,
                                color = CryptoTrackerTheme.colors.onSurface,
                            )
                            Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.small))
                            Text(
                                text = stringResource(Res.string.search_no_results_hint),
                                style = CryptoTrackerTheme.typography.bodyMedium,
                                color = CryptoTrackerTheme.colors.onSurfaceVariant,
                            )
                        }
                    } else {
                        Box {
                            LazyColumn(
                                modifier =
                                    Modifier
                                        .fillMaxSize(),
                            ) {
                                items(
                                    items = displayedCoins,
                                    key = { coin -> coin.id },
                                ) { coin ->
                                    CoinListItem(
                                        animatedPaneScope = animatedPaneScope,
                                        coin = coin,
                                        isSelected = coin.id == state.selectedCoinUi?.id,
                                        shouldExistSharedElementTransition = shouldExistSharedElementTransition,
                                        onItemClick = { onAction(CoinListAction.OnCoinClicked(coinUi = coin)) },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }

                                if (state.searchQuery.isBlank()) {
                                    item {
                                        when {
                                            state.isLoadingMore -> {
                                                Box(
                                                    modifier =
                                                        Modifier.fillMaxWidth().padding(CryptoTrackerTheme.spacing.medium),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    CircularProgressIndicator()
                                                }
                                            }

                                            !state.hasMoreCoins -> {
                                                Box(
                                                    modifier =
                                                        Modifier.fillMaxWidth().padding(CryptoTrackerTheme.spacing.medium),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = stringResource(Res.string.coins_all_loaded),
                                                        style = CryptoTrackerTheme.typography.bodySmall,
                                                        color = CryptoTrackerTheme.colors.onSurfaceVariant,
                                                    )
                                                }
                                            }

                                            else -> {
                                                Box(
                                                    modifier =
                                                        Modifier.fillMaxWidth().padding(CryptoTrackerTheme.spacing.medium),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Button(onClick = { onAction(CoinListAction.OnLoadMore) }) {
                                                        Text(stringResource(Res.string.load_more))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Shimmer sweeps over the real list while a manual refresh is in
                            // flight — rows stay exactly as they were, never re-keyed or re-animated.
                            ShimmerOverlay(isShimmering = state.isManualRefreshing)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoinListHeader(modifier: Modifier = Modifier) {
    val headerStyle =
        CryptoTrackerTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = CryptoTrackerTheme.colors.onSurfaceVariant,
        )

    Column(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = CryptoTrackerTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.coin_list_header_asset).uppercase(),
                modifier = Modifier.weight(ASSET_COLUMN_WEIGHT),
                style = headerStyle,
                textAlign = TextAlign.Start,
            )

            Row(
                modifier = Modifier.weight(PRICE_COLUMN_WEIGHT),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.coin_list_header_price).uppercase(),
                    style = headerStyle,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = CryptoTrackerTheme.colors.primary,
                    modifier = Modifier.size(CryptoTrackerTheme.sizing.iconSmall),
                )
            }

            Text(
                text = stringResource(Res.string.coin_list_header_trend).uppercase(),
                modifier = Modifier.weight(TREND_COLUMN_WEIGHT),
                style = headerStyle,
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(Res.string.coin_list_header_change_pct).uppercase(),
                modifier =
                    Modifier
                        .weight(CHANGE_COLUMN_WEIGHT)
                        // Matches the min width on the data column's chip container below,
                        // so the header stays aligned with it at every row width.
                        .widthIn(min = CryptoTrackerTheme.sizing.coinListChangeChipMinWidth),
                style = headerStyle,
                textAlign = TextAlign.End,
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = CryptoTrackerTheme.sizing.dividerThickness,
            color = CryptoTrackerTheme.colors.onSurfaceVariant.copy(alpha = 0.12f),
        )
        Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.extraSmall))
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, name = "Loading")
@Composable
private fun CoinListScreenLoadingPreview() {
    CryptoTrackerThemeProvider(darkTheme = false) {
        SharedTransitionLayout {
            CoinListScreen(
                state = CoinListState(isLoading = true),
                shouldExistSharedElementTransition = false,
                onAction = {},
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, name = "Loaded with coins")
@Composable
private fun CoinListScreenLoadedPreview() {
    CryptoTrackerThemeProvider(darkTheme = false) {
        SharedTransitionLayout {
            CoinListScreen(
                state =
                    CoinListState(
                        isLoading = false,
                        coins = (0..4).map { previewCoin.copy(id = it.toString()) },
                    ),
                shouldExistSharedElementTransition = false,
                onAction = {},
            )
        }
    }
}
