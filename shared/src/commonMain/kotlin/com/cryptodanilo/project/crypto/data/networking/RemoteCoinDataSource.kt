package com.cryptodanilo.project.crypto.data.networking

import com.cryptodanilo.project.core.data.networking.constructUrl
import com.cryptodanilo.project.core.data.networking.safeCall
import com.cryptodanilo.project.core.database.CoinDao
import com.cryptodanilo.project.core.database.CryptoDatabase
import com.cryptodanilo.project.core.database.toCoinEntity
import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
import com.cryptodanilo.project.core.domain.util.map
import com.cryptodanilo.project.core.util.getCurrentTimeMs
import com.cryptodanilo.project.crypto.data.mappers.toCoin
import com.cryptodanilo.project.crypto.data.mappers.toCoinPrice
import com.cryptodanilo.project.crypto.data.mappers.toMarket
import com.cryptodanilo.project.crypto.data.networking.dto.CoinHistoryDto
import com.cryptodanilo.project.crypto.data.networking.dto.CoinsResponseDto
import com.cryptodanilo.project.crypto.data.networking.dto.MarketsResponseDto
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.domain.CoinDataSource
import com.cryptodanilo.project.crypto.domain.CoinPrice
import com.cryptodanilo.project.crypto.domain.Market
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime
import com.cryptodanilo.project.core.database.toCoin as entityToCoin

class RemoteCoinDataSource(
    private val httpClient: HttpClient,
    private val coinDao: CoinDao,
) : CoinDataSource {
    override suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): Result<List<Coin>, NetworkError> {
        val now = getCurrentTimeMs()
        val lastCachedAt = coinDao.getLastCachedAt()
        val isCacheValid =
            lastCachedAt != null &&
                (now - lastCachedAt) < CryptoDatabase.CACHE_TTL_MS &&
                coinDao.getCount() > 0

        if (isCacheValid) {
            val cached = coinDao.getCoins(limit, offset)
            if (cached.isNotEmpty()) {
                return Result.Success(cached.map { it.entityToCoin() })
            }
        }

        return fetchAndCache(limit, offset)
    }

    override suspend fun getLastCachedAt(): Long? = coinDao.getLastCachedAt()

    override suspend fun forceRefresh(limit: Int): Result<List<Coin>, NetworkError> {
        coinDao.deleteAllCoins()
        return fetchAndCache(limit, 0)
    }

    private suspend fun fetchAndCache(
        limit: Int,
        offset: Int,
    ): Result<List<Coin>, NetworkError> {
        val networkResult =
            safeCall<CoinsResponseDto> {
                httpClient.get(constructUrl("/assets")) {
                    parameter("limit", limit)
                    parameter("offset", offset)
                }
            }.map { response -> response.data.map { it.toCoin() } }

        when (networkResult) {
            is Result.Success -> {
                val cachedAt = getCurrentTimeMs()
                if (offset == 0) coinDao.deleteAllCoins()
                coinDao.insertCoins(networkResult.data.map { it.toCoinEntity(cachedAt) })
            }
            is Result.Error -> {
                val staleCache = coinDao.getCoins(limit, offset)
                if (staleCache.isNotEmpty()) {
                    return Result.Success(staleCache.map { it.entityToCoin() })
                }
            }
        }

        return networkResult
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime,
        interval: String,
    ): Result<List<CoinPrice>, NetworkError> {
        val startMillis = start.toInstant(TimeZone.UTC).toEpochMilliseconds()
        val endMillis = end.toInstant(TimeZone.UTC).toEpochMilliseconds()
        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = constructUrl("/assets/$coinId/history"),
            ) {
                parameter("interval", interval)
                parameter("start", startMillis)
                parameter("end", endMillis)
            }
        }.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }

    override suspend fun getMarkets(
        assetId: String,
        limit: Int,
        offset: Int,
    ): Result<List<Market>, NetworkError> =
        safeCall<MarketsResponseDto> {
            httpClient.get(constructUrl("/markets")) {
                parameter("assetId", assetId)
                parameter("limit", limit)
                parameter("offset", offset)
            }
        }.map { response ->
            response.data.map { it.toMarket() }
        }
}
