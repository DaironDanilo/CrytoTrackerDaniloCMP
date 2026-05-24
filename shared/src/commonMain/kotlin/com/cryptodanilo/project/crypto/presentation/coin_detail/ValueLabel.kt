package com.cryptodanilo.project.crypto.presentation.coin_detail

data class ValueLabel(
    val value: Float,
    val unit: String
) {
    fun formatted(): String {
        val fractionDigits = when {
            value > 1000 -> 0
            value in 2f..999f -> 2
            else -> 3
        }

        val formatted = value.let { number ->
            val integerPart =
                number.toLong().toString().reversed().chunked(3).joinToString(",").reversed()
            val fractionalPart =
                ((number - number.toLong()) * 100).toInt().toString().padStart(fractionDigits, '0')
            when (fractionDigits) {
                0 -> integerPart
                2 -> "$integerPart.$fractionalPart"
                else -> "$integerPart.$fractionalPart"
            }
        }

        return "$formatted $unit"
    }
}
