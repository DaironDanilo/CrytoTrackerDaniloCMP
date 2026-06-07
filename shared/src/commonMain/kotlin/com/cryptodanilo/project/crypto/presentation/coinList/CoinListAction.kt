package com.cryptodanilo.project.crypto.presentation.coinList

import com.cryptodanilo.project.crypto.presentation.coinDetail.DetailTab
import com.cryptodanilo.project.crypto.presentation.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinClicked(
        val coinUi: CoinUi,
    ) : CoinListAction

    data object OnRefresh : CoinListAction

    data class OnDetailTabSelected(
        val tab: DetailTab,
    ) : CoinListAction

    data object OnRetryMarkets : CoinListAction

    data class OnSearchQueryChange(
        val query: String,
    ) : CoinListAction

    data object OnLoadMore : CoinListAction
}
