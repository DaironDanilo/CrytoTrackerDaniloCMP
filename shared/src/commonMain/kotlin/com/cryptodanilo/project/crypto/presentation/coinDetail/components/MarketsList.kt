package com.cryptodanilo.project.crypto.presentation.coinDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import cryptotrackerdanilo.shared.generated.resources.markets_empty
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

        state.marketsError != null -> {
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
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun MarketsContent(
    state: CoinListState,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isCompact = maxWidth < CryptoTrackerTheme.sizing.marketsListCompactBreakpoint
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (!isCompact) {
                item {
                    MarketsColumnHeader()
                    HorizontalDivider(color = CryptoTrackerTheme.colors.outlineVariant)
                }
            }
            items(
                items = state.markets,
                key = { market -> "${market.exchangeId}_${market.pair}" },
            ) { market ->
                MarketListItem(
                    market = market,
                    isCompact = isCompact,
                )
                HorizontalDivider(color = CryptoTrackerTheme.colors.outlineVariant)
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
