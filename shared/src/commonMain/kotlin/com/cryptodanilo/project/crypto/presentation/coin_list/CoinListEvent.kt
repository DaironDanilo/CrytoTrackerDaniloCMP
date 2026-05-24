package com.cryptodanilo.project.crypto.presentation.coin_list

import com.cryptodanilo.project.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError) : CoinListEvent
}
