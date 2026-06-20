package com.cryptodanilo.project.core.presentation.util

import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

data class DisplayableNumber(
    val value: Double,
    val formatted: String,
)

fun Double.toDisplayableNumber(): DisplayableNumber {
    val sign = if (this < 0) "-" else ""
    val abs = kotlin.math.abs(this)
    val integerPart =
        abs
            .toLong()
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    val fractionalPart = ((abs - abs.toLong()) * 100).toLong().toString().padStart(2, '0')
    return DisplayableNumber(value = this, formatted = "$sign$integerPart.$fractionalPart")
}

fun Double.toAbbreviatedDollarString(): String {
    val (divisor, suffix) =
        when {
            this >= 1_000_000_000_000 -> 1_000_000_000_000.0 to "T"
            this >= 1_000_000_000 -> 1_000_000_000.0 to "B"
            this >= 1_000_000 -> 1_000_000.0 to "M"
            this >= 1_000 -> 1_000.0 to "K"
            else -> return "$ ${this.toDisplayableNumber().formatted}"
        }
    val divided = kotlin.math.abs(this / divisor)
    val intPart = divided.toLong()
    val fracPart = ((divided - intPart) * 100).roundToInt()
    val (finalIntPart, finalFracPart) =
        if (fracPart == 100) {
            (intPart + 1) to 0
        } else {
            intPart to fracPart
        }
    val fracPartString = finalFracPart.toString().padStart(2, '0')
    return "$ $finalIntPart.${fracPartString}$suffix"
}

fun Long.toAbbreviatedString(): String =
    when {
        this >= 1_000_000L -> {
            val d = this / 1_000_000.0
            val i = d.toLong()
            val f = ((d - i) * 10).toInt()
            "$i.${f}M"
        }
        this >= 1_000L -> {
            val d = this / 1_000.0
            val i = d.toLong()
            val f = ((d - i) * 10).toInt()
            "$i.${f}K"
        }
        else -> this.toString()
    }

private fun Double.toCommaFormattedString(decimals: Int): String {
    val sign = if (this < 0) "-" else ""
    val abs = kotlin.math.abs(this)
    val factor = 10.0.pow(decimals)
    val rounded = (abs * factor).roundToLong()
    val factorLong = factor.toLong()
    val integerPart = rounded / factorLong
    val fractionalPart = rounded % factorLong
    val integerFormatted =
        integerPart
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    return if (decimals == 0) {
        "$sign$integerFormatted"
    } else {
        "$sign$integerFormatted.${fractionalPart.toString().padStart(decimals, '0')}"
    }
}

// Always shows the full number with thousand separators — used for the coin list,
// where prices must never be abbreviated. Small altcoin prices need more decimal
// places than whole-dollar prices to remain meaningful (e.g. 0.000142).
fun Double.formatFullPrice(): String {
    val abs = kotlin.math.abs(this)
    val decimals =
        when {
            abs < 0.01 -> 6
            abs < 1.0 -> 4
            else -> 2
        }
    return toCommaFormattedString(decimals)
}

// Abbreviates large numbers with a K/M/B/T suffix — used for the CoinDetail stat
// cards, where space is constrained and exact precision matters less than scannability.
fun Double.formatAbbreviatedPrice(): String {
    val abs = kotlin.math.abs(this)
    val divisorAndSuffix =
        when {
            abs >= 1_000_000_000_000.0 -> 1_000_000_000_000.0 to "T"
            abs >= 1_000_000_000.0 -> 1_000_000_000.0 to "B"
            abs >= 1_000_000.0 -> 1_000_000.0 to "M"
            abs >= 1_000.0 -> 1_000.0 to "K"
            else -> null
        } ?: return toCommaFormattedString(2)
    val (divisor, suffix) = divisorAndSuffix
    return "${(this / divisor).toCommaFormattedString(2)}$suffix"
}

// Formats an absolute price change with an explicit leading sign before the
// dollar symbol (e.g. "+$983.35", "-$156.42"). Values that round to zero are
// shown as neutral "$0.00" rather than "-$0.00" or "+$0.00".
fun Double.formatPriceChange(): String {
    val abs = kotlin.math.abs(this)
    if (abs < 0.005) return "\$ 0.00"
    val sign = if (this > 0) "+" else "-"
    return "$sign\$ ${abs.toCommaFormattedString(2)}"
}

fun Double.toPercentString(): String {
    val abs = kotlin.math.abs(this)
    val intPart = abs.toLong()
    val fracPart = ((abs - intPart) * 10).toInt()
    val sign = if (this < 0) "-" else ""
    return "$sign$intPart.$fracPart%"
}
