package com.cryptodanilo.project.crypto.presentation.models

import com.cryptodanilo.project.core.presentation.util.DisplayableNumber
import com.cryptodanilo.project.core.presentation.util.toAbbreviatedDollarString
import com.cryptodanilo.project.core.presentation.util.toAbbreviatedString
import com.cryptodanilo.project.core.presentation.util.toDisplayableNumber
import com.cryptodanilo.project.core.presentation.util.toPercentString
import com.cryptodanilo.project.core.presentation.util.toRelativeTimeString
import com.cryptodanilo.project.crypto.domain.Market

data class MarketUi(
    val rank: Int,
    val exchangeId: String,
    val pair: String,
    val priceUsd: DisplayableNumber,
    val volumeUsd24Hr: String,
    val percentExchangeVolume: String,
    val tradesCount24Hr: String,
    val updated: String,
)

fun Market.toMarketUi(): MarketUi =
    MarketUi(
        rank = rank,
        exchangeId = exchangeId,
        pair = "$baseSymbol/$quoteSymbol",
        priceUsd = priceUsd.toDisplayableNumber(),
        volumeUsd24Hr = volumeUsd24Hr.toAbbreviatedDollarString(),
        percentExchangeVolume = percentExchangeVolume.toPercentString(),
        tradesCount24Hr = tradesCount24Hr.toAbbreviatedString(),
        updated = updated.toRelativeTimeString(),
    )

// Formatting helpers used by the compact market row.
// Labels are passed in so these remain pure (non-Composable) and testable.
fun MarketUi.pairWithTradesLine(tradesLabel: String): String = "$pair · $tradesCount24Hr $tradesLabel"

fun MarketUi.volumeWithPercentLine(volLabel: String): String = "$volumeUsd24Hr · $percentExchangeVolume $volLabel"
