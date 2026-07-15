package com.cryptodanilo.project.crypto.presentation.coinList

import com.cryptodanilo.project.crypto.presentation.coinDetail.ChartTimeframe
import com.cryptodanilo.project.crypto.presentation.coinDetail.DetailTab
import com.cryptodanilo.project.crypto.presentation.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinClicked(
        val coinUi: CoinUi,
        // True when the list and detail panes are both visible side by side. In that
        // layout, switching coins should keep the currently selected detail tab —
        // it's a session-level UI preference, not something tied to a specific coin.
        val isDualPane: Boolean = false,
    ) : CoinListAction

    data object OnRefresh : CoinListAction

    data object OnManualRefresh : CoinListAction

    data object OnDetailManualRefresh : CoinListAction

    data class OnDetailTabSelected(
        val tab: DetailTab,
    ) : CoinListAction

    data class OnTimeframeSelected(
        val timeframe: ChartTimeframe,
    ) : CoinListAction

    data object OnRetryChartLoad : CoinListAction

    data object OnRetryMarkets : CoinListAction

    data class OnSearchQueryChange(
        val query: String,
    ) : CoinListAction

    data object OnLoadMore : CoinListAction

    data object OnLoadMoreMarkets : CoinListAction

    data object OnCoinsLoaded : CoinListAction
}
