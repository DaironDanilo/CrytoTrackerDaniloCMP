package com.cryptodanilo.project.crypto.data.mappers

import com.cryptodanilo.project.crypto.data.networking.dto.MarketDto
import com.cryptodanilo.project.crypto.domain.Market

fun MarketDto.toMarket(): Market =
    Market(
        exchangeId = exchangeId,
        rank = rank.toIntOrNull() ?: 0,
        baseSymbol = baseSymbol,
        quoteSymbol = quoteSymbol,
        baseId = baseId,
        quoteId = quoteId,
        priceUsd = priceUsd.toDoubleOrNull() ?: 0.0,
        volumeUsd24Hr = volumeUsd24Hr.toDoubleOrNull() ?: 0.0,
        percentExchangeVolume = percentExchangeVolume.toDoubleOrNull() ?: 0.0,
        tradesCount24Hr = tradesCount24Hr ?: 0L,
        updated = updated,
    )
