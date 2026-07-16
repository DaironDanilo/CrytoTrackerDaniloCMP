package com.cryptodanilo.project.crypto.data.networking.server

import com.cryptodanilo.project.core.Candle
import com.cryptodanilo.project.core.data.networking.safeCall
import com.cryptodanilo.project.core.database.CoinDao
import com.cryptodanilo.project.core.database.CoinPriceDao
import com.cryptodanilo.project.core.database.CryptoDatabase
import com.cryptodanilo.project.core.database.toCoinEntity
import com.cryptodanilo.project.core.database.toCoinPriceEntity
import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
import com.cryptodanilo.project.core.domain.util.map
import com.cryptodanilo.project.core.util.getCurrentTimeMs
import com.cryptodanilo.project.crypto.data.mappers.toCoin
import com.cryptodanilo.project.crypto.data.mappers.toCoinPrice
import com.cryptodanilo.project.crypto.data.mappers.toMarket
import com.cryptodanilo.project.crypto.data.networking.server.dto.CoinsPageDto
import com.cryptodanilo.project.crypto.data.networking.server.dto.MarketsPageDto
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.domain.CoinDataSource
import com.cryptodanilo.project.crypto.domain.CoinPrice
import com.cryptodanilo.project.crypto.domain.Market
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDateTime
import com.cryptodanilo.project.core.database.toCoin as entityToCoin
import com.cryptodanilo.project.core.database.toCoinPrice as entityToCoinPrice

// :server now runs on Cloud Run (min-instances=0, built/pushed from the
// Ubuntu box, no Cloud Build), reachable from anywhere rather than only
// the home LAN. Swap this out for a BuildKonfig-driven value if
// per-environment URLs are ever needed.
private const val BASE_URL = "https://cryptotracker-server-560504442386.us-central1.run.app/api/v1"

private const val COINS_SEGMENT = "coins"
private const val HISTORY_SEGMENT = "history"
private const val MARKETS_SEGMENT = "markets"

private const val PARAM_LIMIT = "limit"
private const val PARAM_OFFSET = "offset"
private const val PARAM_RANGE = "range"

class ServerCoinDataSource(
    private val httpClient: HttpClient,
    private val coinDao: CoinDao,
    private val coinPriceDao: CoinPriceDao,
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

        return fetchAndCacheCoins(limit, offset)
    }

    override suspend fun getLastCachedAt(): Long? = coinDao.getLastCachedAt()

    override suspend fun forceRefresh(limit: Int): Result<List<Coin>, NetworkError> {
        coinDao.deleteAllCoins()
        return fetchAndCacheCoins(limit, 0)
    }

    private suspend fun fetchAndCacheCoins(
        limit: Int,
        offset: Int,
    ): Result<List<Coin>, NetworkError> {
        val networkResult =
            safeCall<CoinsPageDto> {
                httpClient.get("$BASE_URL/$COINS_SEGMENT") {
                    parameter(PARAM_LIMIT, limit)
                    parameter(PARAM_OFFSET, offset)
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

    override suspend fun getCoinHistory(
        coinId: String,
        timeframe: String,
        start: LocalDateTime,
        end: LocalDateTime,
        interval: String,
    ): Result<List<CoinPrice>, NetworkError> {
        val now = getCurrentTimeMs()
        val lastCachedAt = coinPriceDao.getCachedAt(coinId, timeframe)
        val isCacheValid =
            lastCachedAt != null &&
                (now - lastCachedAt) < CryptoDatabase.CACHE_TTL_MS &&
                coinPriceDao.getCount(coinId, timeframe) > 0

        if (isCacheValid) {
            val cached = coinPriceDao.getPriceHistory(coinId, timeframe)
            if (cached.isNotEmpty()) {
                return Result.Success(cached.map { it.entityToCoinPrice() })
            }
        }

        return fetchAndCacheHistory(coinId, timeframe)
    }

    override suspend fun forceRefreshCoinHistory(
        coinId: String,
        timeframe: String,
        start: LocalDateTime,
        end: LocalDateTime,
        interval: String,
    ): Result<List<CoinPrice>, NetworkError> {
        coinPriceDao.deletePriceHistory(coinId, timeframe)
        return fetchAndCacheHistory(coinId, timeframe)
    }

    override suspend fun getLastHistoryCachedAt(
        coinId: String,
        timeframe: String,
    ): Long? = coinPriceDao.getCachedAt(coinId, timeframe)

    /**
     * [timeframe] is [ChartTimeframe.label] ("1D"/"5D"/"1M"/"6M"/"YTD"/"1Y"),
     * which lowercased is exactly the `range` value :server's history route
     * expects -- no date-math needed on this side, the server picks
     * granularity and applies a point cap per range.
     */
    private suspend fun fetchAndCacheHistory(
        coinId: String,
        timeframe: String,
    ): Result<List<CoinPrice>, NetworkError> {
        val networkResult =
            safeCall<List<Candle>> {
                httpClient.get("$BASE_URL/$COINS_SEGMENT/$coinId/$HISTORY_SEGMENT") {
                    parameter(PARAM_RANGE, timeframe.lowercase())
                }
            }.map { candles -> candles.map { it.toCoinPrice() } }

        when (networkResult) {
            is Result.Success -> {
                val cachedAt = getCurrentTimeMs()
                coinPriceDao.replacePriceHistory(
                    coinId,
                    timeframe,
                    networkResult.data.map { it.toCoinPriceEntity(coinId, timeframe, cachedAt) },
                )
            }
            is Result.Error -> {
                val staleCache = coinPriceDao.getPriceHistory(coinId, timeframe)
                if (staleCache.isNotEmpty()) {
                    return Result.Success(staleCache.map { it.entityToCoinPrice() })
                }
            }
        }

        return networkResult
    }

    override suspend fun getMarkets(
        assetId: String,
        limit: Int,
        offset: Int,
    ): Result<List<Market>, NetworkError> =
        safeCall<MarketsPageDto> {
            httpClient.get("$BASE_URL/$COINS_SEGMENT/$assetId/$MARKETS_SEGMENT") {
                parameter(PARAM_LIMIT, limit)
                parameter(PARAM_OFFSET, offset)
            }
        }.map { response ->
            response.data.map { it.toMarket(assetId) }
        }
}
