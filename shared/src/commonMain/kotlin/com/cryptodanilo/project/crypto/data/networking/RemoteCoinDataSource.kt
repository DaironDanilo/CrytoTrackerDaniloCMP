package com.cryptodanilo.project.crypto.data.networking

import com.cryptodanilo.project.core.data.networking.constructUrl
import com.cryptodanilo.project.core.data.networking.safeCall
import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
import com.cryptodanilo.project.core.domain.util.map
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

class RemoteCoinDataSource(
    private val httpClient: HttpClient,
) : CoinDataSource {
    override suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): Result<List<Coin>, NetworkError> =
        safeCall<CoinsResponseDto> {
            httpClient.get(constructUrl("/assets")) {
                parameter("limit", limit)
                parameter("offset", offset)
            }
        }.map { response ->
            response.data.map { it.toCoin() }
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Result<List<CoinPrice>, NetworkError> {
        val startMillis = start.toInstant(TimeZone.UTC).toEpochMilliseconds()
        val endMillis = end.toInstant(TimeZone.UTC).toEpochMilliseconds()
        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = constructUrl("/assets/$coinId/history"),
            ) {
                parameter("interval", "h6")
                parameter("start", startMillis)
                parameter("end", endMillis)
            }
        }.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }

    override suspend fun getCoinMarkets(coinId: String): Result<List<Market>, NetworkError> =
        safeCall<MarketsResponseDto> {
            httpClient.get(
                urlString = constructUrl("/markets"),
            ) {
                parameter("baseId", coinId)
                parameter("limit", 100)
                parameter("offset", 0)
            }
        }.map { response ->
            response.data.map { it.toMarket() }
        }
}
