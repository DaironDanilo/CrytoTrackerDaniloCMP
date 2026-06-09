package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.core.presentation.util.toRelativeTimeString
import com.cryptodanilo.project.core.util.MILLIS_PER_HOUR
import com.cryptodanilo.project.core.util.MILLIS_PER_MINUTE
import com.cryptodanilo.project.core.util.getCurrentTimeMs
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coinList.CoinListState
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.coin_list_error
import cryptotrackerdanilo.shared.generated.resources.coins_all_loaded
import cryptotrackerdanilo.shared.generated.resources.load_more
import cryptotrackerdanilo.shared.generated.resources.retry
import cryptotrackerdanilo.shared.generated.resources.updated_timestamp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

private const val MIN_TICKER_DELAY_MS = 100L

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
                    LastUpdatedText(
                        updatedAt = updatedAt,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = CryptoTrackerTheme.spacing.medium,
                                    vertical = CryptoTrackerTheme.spacing.extraSmall,
                                ),
                    )
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                ) {
                    items(
                        items = displayedCoins,
                        key = { coin -> coin.id },
                    ) { coin ->
                        Column {
                            CoinListItem(
                                animatedPaneScope = animatedPaneScope,
                                coin = coin,
                                isSelected = coin.id == state.selectedCoinUi?.id,
                                shouldExistSharedElementTransition = shouldExistSharedElementTransition,
                                onItemClick = { onAction(CoinListAction.OnCoinClicked(coinUi = coin)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            HorizontalDivider()
                        }
                    }

                    if (state.searchQuery.isBlank()) {
                        item {
                            when {
                                state.isLoadingMore -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(CryptoTrackerTheme.spacing.medium),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                !state.hasMoreCoins -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(CryptoTrackerTheme.spacing.medium),
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
                                        modifier = Modifier.fillMaxWidth().padding(CryptoTrackerTheme.spacing.medium),
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
            }
        }
    }
}

@Composable
private fun LastUpdatedText(
    updatedAt: Long,
    modifier: Modifier = Modifier,
) {
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(updatedAt) {
        while (true) {
            val diffMs = getCurrentTimeMs() - updatedAt
            val delayMs =
                when {
                    diffMs < MILLIS_PER_MINUTE -> MILLIS_PER_MINUTE - diffMs
                    diffMs < MILLIS_PER_HOUR -> MILLIS_PER_MINUTE - (diffMs % MILLIS_PER_MINUTE)
                    else -> MILLIS_PER_HOUR - (diffMs % MILLIS_PER_HOUR)
                }
            delay(delayMs.coerceAtLeast(MIN_TICKER_DELAY_MS).milliseconds)
            tick++
        }
    }
    val relativeTime = remember(tick, updatedAt) { updatedAt.toRelativeTimeString() }
    Text(
        text = stringResource(Res.string.updated_timestamp, relativeTime),
        style = CryptoTrackerTheme.typography.bodySmall,
        color = CryptoTrackerTheme.colors.onSurfaceVariant,
        modifier = modifier,
    )
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
