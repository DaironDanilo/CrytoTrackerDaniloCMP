package com.cryptodanilo.project.crypto.presentation.coinList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptodanilo.project.core.domain.util.onError
import com.cryptodanilo.project.core.domain.util.onSuccess
import com.cryptodanilo.project.core.presentation.util.toUiString
import com.cryptodanilo.project.crypto.domain.CoinDataSource
import com.cryptodanilo.project.crypto.presentation.coinDetail.DataPoint
import com.cryptodanilo.project.crypto.presentation.coinDetail.DetailTab
import com.cryptodanilo.project.crypto.presentation.models.CoinUi
import com.cryptodanilo.project.crypto.presentation.models.toCoinUi
import com.cryptodanilo.project.crypto.presentation.models.toMarketUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

class CoinListViewModel(
    private val coinDataSource: CoinDataSource,
) : ViewModel() {
    private val _state = MutableStateFlow(CoinListState())
    val state: StateFlow<CoinListState> = _state

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    private var currentOffset = 0
    private var currentMarketsOffset = 0

    init {
        loadCoins()
    }

    fun onAction(action: CoinListAction) {
        when (action) {
            CoinListAction.OnRefresh -> refresh()
            CoinListAction.OnManualRefresh -> manualRefresh()
            is CoinListAction.OnCoinClicked -> selectCoin(action.coinUi)
            is CoinListAction.OnDetailTabSelected -> onDetailTabSelected(action.tab)
            CoinListAction.OnRetryMarkets -> loadInitialMarkets()
            CoinListAction.OnLoadMoreMarkets -> loadMoreMarkets()
            is CoinListAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
            CoinListAction.OnLoadMore -> loadMore()
            CoinListAction.OnCoinsLoaded -> {
                if (_state.value.selectedCoinUi == null && _state.value.coins.isNotEmpty()) {
                    selectCoin(_state.value.coins.first())
                }
            }
        }
    }

    private fun loadCoins() {
        _state.update { it.copy(isLoading = true, isError = false) }
        viewModelScope.launch {
            val result = coinDataSource.getCoins(limit = PAGE_SIZE, offset = 0)
            val lastCachedAt = coinDataSource.getLastCachedAt()
            result
                .onSuccess { coins ->
                    currentOffset = PAGE_SIZE
                    val coinUis = coins.map { it.toCoinUi() }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            coins = coinUis,
                            hasMoreCoins = coins.size >= PAGE_SIZE,
                            lastUpdatedMs = lastCachedAt,
                        )
                    }
                }.onError { error ->
                    _state.update { it.copy(isLoading = false, isError = true) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    // Pull-to-refresh gesture — drives PullToRefreshBox's indicator only.
    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, isError = false) }
            forceRefreshInternal()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    // Manual [↺] button tap — drives the button's own spinner only.
    private fun manualRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(isManualRefreshing = true, isError = false) }
            forceRefreshInternal()
            _state.update { it.copy(isManualRefreshing = false) }
        }
    }

    // Shared by both refresh() and manualRefresh() — keeps the current list on screen
    // while it refreshes (so neither trigger blanks the list behind the full-screen
    // isLoading spinner) and uses forceRefresh() rather than getCoins() because
    // getCoins() serves straight from the Room cache while it's still within
    // CACHE_TTL_MS — a "refresh" must always hit the network.
    private suspend fun forceRefreshInternal() {
        val result = coinDataSource.forceRefresh(limit = PAGE_SIZE)
        val lastCachedAt = coinDataSource.getLastCachedAt()
        result
            .onSuccess { coins ->
                currentOffset = PAGE_SIZE
                val coinUis = coins.map { it.toCoinUi() }
                _state.update {
                    it.copy(
                        coins = coinUis,
                        hasMoreCoins = coins.size >= PAGE_SIZE,
                        lastUpdatedMs = lastCachedAt,
                    )
                }
            }.onError { error ->
                _state.update { it.copy(isError = true) }
                _events.send(CoinListEvent.Error(error))
            }
    }

    private fun loadMore() {
        if (_state.value.isLoadingMore || !_state.value.hasMoreCoins) return
        _state.update { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            val result = coinDataSource.getCoins(limit = PAGE_SIZE, offset = currentOffset)
            val lastCachedAt = coinDataSource.getLastCachedAt()
            result
                .onSuccess { coins ->
                    currentOffset += PAGE_SIZE
                    _state.update {
                        it.copy(
                            isLoadingMore = false,
                            coins = it.coins + coins.map { coin -> coin.toCoinUi() },
                            hasMoreCoins = coins.size >= PAGE_SIZE,
                            lastUpdatedMs = lastCachedAt ?: it.lastUpdatedMs,
                        )
                    }
                }.onError { error ->
                    _state.update { it.copy(isLoadingMore = false) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun onDetailTabSelected(tab: DetailTab) {
        _state.update { it.copy(selectedDetailTab = tab) }
        if (tab == DetailTab.Markets && _state.value.markets.isEmpty()) {
            loadInitialMarkets()
        }
    }

    private fun loadInitialMarkets() {
        val coinId = _state.value.selectedCoinUi?.id ?: return
        currentMarketsOffset = 0
        _state.update {
            it.copy(
                isMarketsLoading = true,
                isLoadingMoreMarkets = false,
                markets = emptyList(),
                hasMoreMarkets = true,
                marketsError = null,
            )
        }
        viewModelScope.launch {
            coinDataSource
                .getMarkets(assetId = coinId, limit = PAGE_SIZE, offset = 0)
                .onSuccess { markets ->
                    currentMarketsOffset = PAGE_SIZE
                    val marketUis = markets.map { it.toMarketUi() }
                    _state.update {
                        it.copy(
                            isMarketsLoading = false,
                            markets = marketUis,
                            hasMoreMarkets = markets.size >= PAGE_SIZE,
                        )
                    }
                }.onError { error ->
                    _state.update { it.copy(isMarketsLoading = false, marketsError = error.toUiString()) }
                }
        }
    }

    private fun loadMoreMarkets() {
        if (_state.value.isLoadingMoreMarkets || !_state.value.hasMoreMarkets) return
        val coinId = _state.value.selectedCoinUi?.id ?: return
        _state.update { it.copy(isLoadingMoreMarkets = true, marketsError = null) }
        viewModelScope.launch {
            coinDataSource
                .getMarkets(assetId = coinId, limit = PAGE_SIZE, offset = currentMarketsOffset)
                .onSuccess { markets ->
                    currentMarketsOffset += PAGE_SIZE
                    _state.update {
                        it.copy(
                            isLoadingMoreMarkets = false,
                            markets = it.markets + markets.map { market -> market.toMarketUi() },
                            hasMoreMarkets = markets.size >= PAGE_SIZE,
                        )
                    }
                }.onError { error ->
                    _state.update { it.copy(isLoadingMoreMarkets = false, marketsError = error.toUiString()) }
                }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun selectCoin(coinUi: CoinUi) {
        currentMarketsOffset = 0
        _state.update {
            it.copy(
                selectedCoinUi = coinUi,
                selectedDetailTab = DetailTab.Chart,
                markets = emptyList(),
                isMarketsLoading = false,
                isLoadingMoreMarkets = false,
                hasMoreMarkets = true,
                marketsError = null,
            )
        }
        viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    start =
                        Clock.System
                            .now()
                            .minus(COIN_HISTORY_DAYS.days)
                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                    end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                ).onSuccess { history ->
                    val dataPoints =
                        history
                            .sortedBy { it.dateTime }
                            .map { coinPrice ->
                                DataPoint(
                                    x = coinPrice.dateTime.hour.toFloat(),
                                    y = coinPrice.priceUsd.toFloat(),
                                    xLabel = formatDateTime(coinPrice.dateTime),
                                )
                            }
                    _state.update { it.copy(selectedCoinUi = it.selectedCoinUi?.copy(coinPriceHistory = dataPoints)) }
                }.onError { networkError ->
                    _events.send(CoinListEvent.Error(networkError))
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val COIN_HISTORY_DAYS = 7
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun formatDateTime(time: LocalDateTime): String {
    val formatDate =
        LocalDateTime.Format {
            amPmHour(Padding.NONE)
            amPmMarker("AM", "PM")
            byUnicodePattern("\nM/d")
        }
    return formatDate.format(time)
}
