package com.cryptodanilo.project.crypto.presentation.coin_detail

import kotlin.math.round

data class ValueLabel(
    val value: Float,
    val unit: String
) {
    fun formatted(): String {
        // Determine the number of fraction digits based on the value
        val fractionDigits = when {
            value > 1000 -> 0
            value in 2f..999f -> 2
            else -> 3
        }

        // Format the value manually by rounding it based on fractionDigits
        val formattedValue = when {
            fractionDigits == 0 -> value.toInt().toString()  // No decimals for large numbers
            fractionDigits == 2 -> round(value * 100) / 100  // Round to 2 decimal places
            else -> round(value * 1000) / 1000  // Round to 3 decimal places
        }

        // Return the formatted string with the unit
        return "$formattedValue $unit"
    }
}
