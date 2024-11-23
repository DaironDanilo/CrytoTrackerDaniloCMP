package com.cryptodanilo.project.crypto.presentation.models

import com.cryptodanilo.project.core.presentation.util.getDrawableIdForCoin
import com.cryptodanilo.project.crypto.domain.Coin
import com.cryptodanilo.project.crypto.presentation.coin_detail.DataPoint
import org.jetbrains.compose.resources.DrawableResource

data class CoinUi(
    val id: String,
    val rank: Int,
    val symbol: String,
    val name: String,
    val marketCapUsd: DisplayableNumber,
    val priceUsd: DisplayableNumber,
    val changePercent24Hr: DisplayableNumber,
    val iconRes: DrawableResource,
    val coinPriceHistory: List<DataPoint> = emptyList(),
)

data class DisplayableNumber(
    val value: Double,
    val formatted: String,
)

fun Coin.toCoinUi(): CoinUi {
    return CoinUi(
        id = id,
        rank = rank,
        symbol = symbol,
        name = name,
        marketCapUsd = marketCapUsd.toDisplayableNumber(),
        priceUsd = priceUsd.toDisplayableNumber(),
        changePercent24Hr = changePercent24Hr.toDisplayableNumber(),
        iconRes = getDrawableIdForCoin(symbol)
    )
}

fun Double.toDisplayableNumber(): DisplayableNumber {
    // Manually formatting the number using string interpolation
    val formatted = this.toString().let { str ->
        val dotIndex = str.indexOf(".")
        if (dotIndex != -1 && str.length > dotIndex + 2) {
            // Limit to two decimals after the point
            str.substring(0, dotIndex + 3) // Keep 2 decimal places
        } else {
            str // Return as is if already formatted
        }
    }

    return DisplayableNumber(value = this, formatted = formatted)
}
