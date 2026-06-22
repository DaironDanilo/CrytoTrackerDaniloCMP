package com.cryptodanilo.project.crypto.domain

import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
import kotlinx.datetime.LocalDateTime

interface CoinDataSource {
    suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): Result<List<Coin>, NetworkError>

    suspend fun getLastCachedAt(): Long?

    suspend fun forceRefresh(limit: Int): Result<List<Coin>, NetworkError>

    suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime,
        interval: String = "h6",
    ): Result<List<CoinPrice>, NetworkError>

    suspend fun getMarkets(
        assetId: String,
        limit: Int,
        offset: Int,
    ): Result<List<Market>, NetworkError>
}
