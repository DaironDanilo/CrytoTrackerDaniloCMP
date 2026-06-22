package com.cryptodanilo.project.crypto.presentation.coinList

import androidx.compose.runtime.Immutable
import com.cryptodanilo.project.core.presentation.util.UiText
import com.cryptodanilo.project.crypto.presentation.coinDetail.ChartTimeframe
import com.cryptodanilo.project.crypto.presentation.coinDetail.DetailTab
import com.cryptodanilo.project.crypto.presentation.models.CoinUi
import com.cryptodanilo.project.crypto.presentation.models.MarketUi

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isManualRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreCoins: Boolean = true,
    val isError: Boolean = false,
    val coins: List<CoinUi> = emptyList(),
    val selectedCoinUi: CoinUi? = null,
    val searchQuery: String = "",
    val selectedDetailTab: DetailTab = DetailTab.Chart,
    val selectedTimeframe: ChartTimeframe = ChartTimeframe.ONE_DAY,
    val isLoadingCoinHistory: Boolean = false,
    val markets: List<MarketUi> = emptyList(),
    val isMarketsLoading: Boolean = false,
    val isLoadingMoreMarkets: Boolean = false,
    val hasMoreMarkets: Boolean = true,
    val marketsError: UiText? = null,
    val lastUpdatedMs: Long? = null,
)
