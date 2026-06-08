package com.cryptodanilo.project.crypto.presentation.coinDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListState
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.load_more
import cryptotrackerdanilo.shared.generated.resources.markets_all_loaded
import cryptotrackerdanilo.shared.generated.resources.markets_empty
import cryptotrackerdanilo.shared.generated.resources.markets_load_more_error
import cryptotrackerdanilo.shared.generated.resources.markets_retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun MarketsList(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isMarketsLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        state.marketsError != null && state.markets.isEmpty() -> {
            Column(
                modifier = modifier.fillMaxSize().padding(CryptoTrackerTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = state.marketsError.asString(),
                    style = CryptoTrackerTheme.typography.bodyMedium,
                    color = CryptoTrackerTheme.colors.error,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = { onAction(CoinListAction.OnRetryMarkets) }) {
                    Text(
                        text = stringResource(Res.string.markets_retry),
                        color = CryptoTrackerTheme.colors.primary,
                    )
                }
            }
        }

        state.markets.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.markets_empty),
                    style = CryptoTrackerTheme.typography.bodyMedium,
                    color = CryptoTrackerTheme.colors.onSurfaceVariant,
                )
            }
        }

        else -> {
            MarketsContent(
                state = state,
                onAction = onAction,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun MarketsContent(
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompact = maxWidth < CryptoTrackerTheme.sizing.marketsListCompactBreakpoint
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (!isCompact) {
                stickyHeader {
                    MarketsColumnHeader()
                    HorizontalDivider(color = CryptoTrackerTheme.colors.outlineVariant)
                }
            }
            itemsIndexed(
                items = state.markets,
                key = { _, market -> "${market.exchangeId}_${market.pair}" },
            ) { index, market ->
                MarketListItem(
                    rank = index + 1,
                    market = market,
                    isCompact = isCompact,
                )
                HorizontalDivider(color = CryptoTrackerTheme.colors.outlineVariant)
            }
            item {
                when {
                    state.isLoadingMoreMarkets -> {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(CryptoTrackerTheme.spacing.medium),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = CryptoTrackerTheme.colors.primary,
                            )
                        }
                    }

                    state.marketsError != null && state.markets.isNotEmpty() -> {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(CryptoTrackerTheme.spacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(Res.string.markets_load_more_error),
                                style = CryptoTrackerTheme.typography.bodySmall,
                                color = CryptoTrackerTheme.colors.error,
                            )
                            Spacer(modifier = Modifier.height(CryptoTrackerTheme.spacing.small))
                            Button(onClick = { onAction(CoinListAction.OnLoadMoreMarkets) }) {
                                Text(stringResource(Res.string.markets_retry))
                            }
                        }
                    }

                    !state.hasMoreMarkets -> {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(CryptoTrackerTheme.spacing.medium),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(Res.string.markets_all_loaded),
                                style = CryptoTrackerTheme.typography.bodySmall,
                                color = CryptoTrackerTheme.colors.onSurfaceVariant,
                            )
                        }
                    }

                    state.hasMoreMarkets && !state.isLoadingMoreMarkets -> {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(CryptoTrackerTheme.spacing.medium),
                            contentAlignment = Alignment.Center,
                        ) {
                            Button(onClick = { onAction(CoinListAction.OnLoadMoreMarkets) }) {
                                Text(stringResource(Res.string.load_more))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Loading")
@Composable
private fun MarketsListLoadingPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        MarketsList(
            state = CoinListState(isMarketsLoading = true),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "With markets")
@Composable
private fun MarketsListPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        MarketsList(
            state =
                CoinListState(
                    markets = (1..3).map { previewMarket.copy(rank = it) },
                ),
            onAction = {},
        )
    }
}
