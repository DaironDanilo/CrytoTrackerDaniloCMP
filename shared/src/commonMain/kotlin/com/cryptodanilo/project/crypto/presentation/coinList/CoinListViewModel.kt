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
import com.cryptodanilo.project.crypto.presentation.models.MarketUi
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

    private var allCoins: List<CoinUi> = emptyList()
    private val marketsCache = HashMap<String, List<MarketUi>>()

    init {
        getCoins()
    }

    fun onAction(action: CoinListAction) {
        when (action) {
            CoinListAction.OnRefresh -> getCoins()
            is CoinListAction.OnCoinClicked -> selectCoin(action.coinUi)
            is CoinListAction.OnDetailTabSelected -> onDetailTabSelected(action.tab)
            CoinListAction.OnRetryMarkets -> loadMarketsForSelectedCoin()
            is CoinListAction.OnSearchQueryChange -> onSearchQueryChange(action.query)
        }
    }

    private fun onDetailTabSelected(tab: DetailTab) {
        _state.update { it.copy(selectedDetailTab = tab) }
        if (tab == DetailTab.Markets) {
            loadMarketsForSelectedCoin()
        }
    }

    private fun loadMarketsForSelectedCoin() {
        val coinId = _state.value.selectedCoinUi?.id ?: return
        val cached = marketsCache[coinId]
        if (cached != null) {
            _state.update { it.copy(markets = cached, isMarketsLoading = false, marketsError = null) }
            return
        }
        _state.update { it.copy(isMarketsLoading = true, marketsError = null) }
        viewModelScope.launch {
            coinDataSource
                .getCoinMarkets(coinId)
                .onSuccess { markets ->
                    val marketUis =
                        markets
                            .sortedByDescending { it.volumeUsd24Hr }
                            .map { it.toMarketUi() }
                    marketsCache[coinId] = marketUis
                    _state.update { it.copy(isMarketsLoading = false, markets = marketUis) }
                }.onError { error ->
                    _state.update { it.copy(isMarketsLoading = false, marketsError = error.toUiString()) }
                }
        }
    }

    private fun onSearchQueryChange(query: String) {
        val filtered =
            if (query.isBlank()) {
                allCoins
            } else {
                allCoins.filter { coin ->
                    coin.symbol.contains(query, ignoreCase = true) ||
                        coin.name.contains(query, ignoreCase = true)
                }
            }
        _state.update { it.copy(searchQuery = query, coins = filtered) }
    }

    @OptIn(ExperimentalTime::class)
    private fun selectCoin(coinUi: CoinUi) {
        _state.update {
            it.copy(
                selectedCoinUi = coinUi,
                selectedDetailTab = DetailTab.Chart,
                markets = emptyList(),
                isMarketsLoading = false,
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
                            .minus(7.days)
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

    private fun getCoins() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    allCoins = coins.map { coin -> coin.toCoinUi() }
                    val query = _state.value.searchQuery
                    val filtered =
                        if (query.isBlank()) {
                            allCoins
                        } else {
                            allCoins.filter { coin ->
                                coin.symbol.contains(query, ignoreCase = true) ||
                                    coin.name.contains(query, ignoreCase = true)
                            }
                        }
                    _state.update { coinListState ->
                        coinListState.copy(
                            isLoading = false,
                            coins = filtered,
                        )
                    }
                }.onError { networkError ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(networkError))
                }
        }
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
