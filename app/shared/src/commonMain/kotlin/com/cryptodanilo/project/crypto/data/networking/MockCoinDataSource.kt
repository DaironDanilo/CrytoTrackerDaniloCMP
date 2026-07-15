package com.cryptodanilo.project.crypto.data.networking

import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
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
import cryptotrackerdanilo.shared.generated.resources.Res
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class MockCoinDataSource : CoinDataSource {
    private val json = Json { ignoreUnknownKeys = true }
    private var cachedCoins: List<Coin>? = null

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadAllCoins(): List<Coin> {
        cachedCoins?.let { return it }
        return try {
            val bytes = Res.readBytes("files/mock/assets/assets_full.json")
            val dto = json.decodeFromString<CoinsResponseDto>(bytes.decodeToString())
            dto.data.map { it.toCoin() }.also { cachedCoins = it }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): Result<List<Coin>, NetworkError> {
        val all = loadAllCoins()
        return if (all.isEmpty()) {
            Result.Error(NetworkError.UNKNOWN)
        } else {
            Result.Success(all.drop(offset).take(limit))
        }
    }

    override suspend fun getLastCachedAt(): Long? = getCurrentTimeMs()

    override suspend fun forceRefresh(limit: Int): Result<List<Coin>, NetworkError> {
        cachedCoins = null
        return getCoins(limit, 0)
    }

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getCoinHistory(
        coinId: String,
        timeframe: String,
        start: LocalDateTime,
        end: LocalDateTime,
        interval: String,
    ): Result<List<CoinPrice>, NetworkError> {
        val fileName = "files/mock/history/history_${coinId}_${timeframe.lowercase()}.json"
        return try {
            val bytes = Res.readBytes(fileName)
            val dto = json.decodeFromString<CoinHistoryDto>(bytes.decodeToString())
            Result.Success(dto.data.map { it.toCoinPrice() })
        } catch (_: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun forceRefreshCoinHistory(
        coinId: String,
        timeframe: String,
        start: LocalDateTime,
        end: LocalDateTime,
        interval: String,
    ): Result<List<CoinPrice>, NetworkError> = getCoinHistory(coinId, timeframe, start, end, interval)

    override suspend fun getLastHistoryCachedAt(
        coinId: String,
        timeframe: String,
    ): Long? = getCurrentTimeMs()

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getMarkets(
        assetId: String,
        limit: Int,
        offset: Int,
    ): Result<List<Market>, NetworkError> =
        try {
            val bytes = Res.readBytes("files/mock/markets/markets.json")
            val dto = json.decodeFromString<MarketsResponseDto>(bytes.decodeToString())
            Result.Success(
                dto.data
                    .drop(offset)
                    .take(limit)
                    .map { it.toMarket() },
            )
        } catch (_: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
}
