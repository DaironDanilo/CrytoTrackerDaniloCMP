package com.cryptodanilo.project.crypto.data.mappers

import com.cryptodanilo.project.crypto.data.networking.dto.CoinDto
import com.cryptodanilo.project.crypto.data.networking.dto.CoinPriceDto
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.domain.CoinPrice
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr
    )
}

@OptIn(ExperimentalTime::class)
fun CoinPriceDto.toCoinPrice(): CoinPrice {
    val instant = Instant.fromEpochMilliseconds(time)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    return CoinPrice(
        priceUsd = priceUsd,
        dateTime = localDateTime
    )
}
