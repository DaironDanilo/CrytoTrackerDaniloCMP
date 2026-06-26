package com.cryptodanilo.project.core.database

import com.cryptodanilo.project.crypto.domain.CoinPrice
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun CoinPriceEntity.toCoinPrice(): CoinPrice =
    CoinPrice(
        priceUsd = priceUsd,
        // Mirror the timezone used in CoinPriceDto.toCoinPrice() so chart x-axis
        // hours are consistent whether the data came from the network or Room.
        dateTime = Instant.fromEpochMilliseconds(timestampMs).toLocalDateTime(TimeZone.currentSystemDefault()),
    )

@OptIn(ExperimentalTime::class)
fun CoinPrice.toCoinPriceEntity(
    coinId: String,
    timeframe: String,
    cachedAt: Long,
): CoinPriceEntity =
    CoinPriceEntity(
        coinId = coinId,
        timeframe = timeframe,
        timestampMs = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        priceUsd = priceUsd,
        cachedAt = cachedAt,
    )
