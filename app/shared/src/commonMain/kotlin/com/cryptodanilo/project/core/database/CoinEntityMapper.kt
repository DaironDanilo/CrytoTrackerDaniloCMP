package com.cryptodanilo.project.core.database

import com.cryptodanilo.project.crypto.domain.Coin

fun CoinEntity.toCoin(): Coin =
    Coin(
        id = id,
        rank = rank,
        symbol = symbol,
        name = name,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr,
    )

fun Coin.toCoinEntity(cachedAt: Long): CoinEntity =
    CoinEntity(
        id = id,
        rank = rank,
        symbol = symbol,
        name = name,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr,
        cachedAt = cachedAt,
    )
