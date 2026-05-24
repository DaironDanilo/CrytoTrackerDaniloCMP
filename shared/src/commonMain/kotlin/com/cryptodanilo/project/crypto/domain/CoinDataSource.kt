package com.cryptodanilo.project.crypto.domain

import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
import kotlinx.datetime.LocalDateTime

interface CoinDataSource {
    suspend fun getCoins(): Result<List<Coin>, NetworkError>
    suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): Result<List<CoinPrice>, NetworkError>
}
