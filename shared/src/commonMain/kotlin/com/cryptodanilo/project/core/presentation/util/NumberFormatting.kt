package com.cryptodanilo.project.core.presentation.util

fun Double.toAbbreviatedDollarString(): String {
    val (divisor, suffix) =
        when {
            this >= 1_000_000_000 -> 1_000_000_000.0 to "B"
            this >= 1_000_000 -> 1_000_000.0 to "M"
            this >= 1_000 -> 1_000.0 to "K"
            else -> return "$ ${this.toLong()}"
        }
    val divided = this / divisor
    val intPart = divided.toLong()
    val fracPart = ((divided - intPart) * 10).toInt()
    return "$ $intPart.${fracPart}$suffix"
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

fun Double.toPercentString(): String {
    val intPart = this.toLong()
    val fracPart = ((this - intPart) * 10).toInt()
    return "$intPart.$fracPart%"
}
