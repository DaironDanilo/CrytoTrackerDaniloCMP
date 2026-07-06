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
    val lastUpdatedDetailMs: Long? = null,
    val isManualRefreshingDetail: Boolean = false,
    val chartHistoryError: Boolean = false,
    val chartRetryCount: Map<ChartTimeframe, Int> = emptyMap(),
    // True only while a top-right refresh is in-flight for a range that was already
    // in an error state. Drives the chart area's loading branch so the error-area
    // spinner shows instead of falling through to a previously-loaded range's data.
    val isRetryingChartFromError: Boolean = false,
)
