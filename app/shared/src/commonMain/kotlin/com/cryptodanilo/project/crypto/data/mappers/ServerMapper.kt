package com.cryptodanilo.project.crypto.data.mappers

import com.cryptodanilo.project.core.Candle
import com.cryptodanilo.project.crypto.data.networking.server.dto.CoinResponseDto
import com.cryptodanilo.project.crypto.data.networking.server.dto.MarketResponseDto
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.domain.CoinPrice
import com.cryptodanilo.project.crypto.domain.Market
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// :server's DTOs don't carry these fields (either optional and absent, or
// CoinCap-era Market fields :server never populates at all) -- these are
// neutral fallbacks, not real data.
private const val DEFAULT_RANK = 0
private const val DEFAULT_MARKET_CAP_USD = 0.0
private const val DEFAULT_CHANGE_PERCENT_24H = 0.0
private const val DEFAULT_PRICE_USD = 0.0
private const val DEFAULT_VOLUME_USD_24H = 0.0
private const val DEFAULT_PERCENT_EXCHANGE_VOLUME = 0.0
private const val DEFAULT_TRADES_COUNT_24H = 0L
private const val DEFAULT_LAST_TRADED_AT = 0L

fun CoinResponseDto.toCoin(): Coin =
    Coin(
        id = id,
        rank = rank ?: DEFAULT_RANK,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd ?: DEFAULT_MARKET_CAP_USD,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24h ?: DEFAULT_CHANGE_PERCENT_24H,
    )

fun MarketResponseDto.toMarket(assetId: String): Market =
    Market(
        exchangeId = exchangeId,
        rank = DEFAULT_RANK,
        baseSymbol = baseSymbol,
        quoteSymbol = targetSymbol,
        baseId = assetId,
        quoteId = targetSymbol.lowercase(),
        priceUsd = priceUsd ?: DEFAULT_PRICE_USD,
        volumeUsd24Hr = volumeUsd24h ?: DEFAULT_VOLUME_USD_24H,
        percentExchangeVolume = DEFAULT_PERCENT_EXCHANGE_VOLUME,
        tradesCount24Hr = DEFAULT_TRADES_COUNT_24H,
        updated = lastTradedAt ?: DEFAULT_LAST_TRADED_AT,
    )

@OptIn(ExperimentalTime::class)
fun Candle.toCoinPrice(): CoinPrice {
    val instant = Instant.fromEpochMilliseconds(openTime)
    return CoinPrice(
        priceUsd = close,
        dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()),
    )
}
