package com.cryptodanilo.project.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptodanilo.project.core.domain.util.onError
import com.cryptodanilo.project.core.domain.util.onSuccess
import com.cryptodanilo.project.crypto.domain.CoinDataSource
import com.cryptodanilo.project.crypto.presentation.coin_detail.DataPoint
import com.cryptodanilo.project.crypto.presentation.models.CoinUi
import com.cryptodanilo.project.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state: StateFlow<CoinListState> = _state

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    init {
        getCoins()
    }

    fun onAction(action: CoinListAction) {
        when (action) {
            CoinListAction.OnRefresh -> getCoins()
            is CoinListAction.OnCoinClicked -> {
//                _state.update { it.copy(selectedCoinUi = action.coinUi) }
                selectCoin(action.coinUi)
            }
        }
    }

    private fun selectCoin(coinUi: CoinUi) {
        _state.update { it.copy(selectedCoinUi = coinUi) }
        viewModelScope.launch {
            coinDataSource.getCoinHistory(
                coinId = coinUi.id,
                start = Clock.System.now().minus(7.days)
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ).onSuccess { history ->
                val dataPoints = history
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
            coinDataSource.getCoins().onSuccess { coins ->
                _state.update { coinListState ->
                    coinListState.copy(isLoading = false,
                        coins = coins.map { coin -> coin.toCoinUi() })
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
    val formatDate = LocalDateTime.Format {
        amPmHour(Padding.NONE)
        amPmMarker("AM", "PM")
        byUnicodePattern("\nM/d")
    }
    return formatDate.format(time)
}
