package com.cryptodanilo.project.crypto.data.networking

import com.cryptodanilo.project.core.data.networking.constructUrl
import com.cryptodanilo.project.core.data.networking.safeCall
import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
import com.cryptodanilo.project.core.domain.util.map
import com.cryptodanilo.project.crypto.data.mappers.toCoin
import com.cryptodanilo.project.crypto.data.mappers.toCoinPrice
import com.cryptodanilo.project.crypto.data.networking.dto.CoinHistoryDto
import com.cryptodanilo.project.crypto.data.networking.dto.CoinsResponseDto
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.domain.CoinDataSource
import com.cryptodanilo.project.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class RemoteCoinDataSource(
    private val httpClient: HttpClient
) : CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinsResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets")
            )
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }

    override suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime
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
}
