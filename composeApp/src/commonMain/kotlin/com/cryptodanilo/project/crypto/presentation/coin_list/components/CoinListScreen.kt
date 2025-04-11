package com.cryptodanilo.project.crypto.presentation.coin_list.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListAction
import com.cryptodanilo.project.crypto.presentation.coin_list.CoinListState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CoinListScreen(
    animatedPaneScope: AnimatedPaneScope,
    state: CoinListState,
    shouldExistSharedElementTransition: Boolean,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.coins, key = { coin -> coin.id }) { coin ->
                CoinListItem(
                    animatedPaneScope = animatedPaneScope,
                    coin = coin,
                    shouldExistSharedElementTransition = shouldExistSharedElementTransition,
                    onItemClick = { onAction(CoinListAction.OnCoinClicked(coinUi = coin)) },
                    modifier = Modifier.fillParentMaxWidth()
                )
                HorizontalDivider()
            }
        }
    }
}

//@PreviewLightDark
//@Composable
//fun CoinListScreenPreview() {
//    CryptoTrackerTheme {
//        CoinListScreen(
//            state = CoinListState(
//                coins = (0..10).map {
//                    previewCoin.copy(id = it.toString())
//                }
//            ),
//            modifier = Modifier.background(MaterialTheme.colorScheme.background),
//            onAction = {}
//        )
//    }
//}
