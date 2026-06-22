package com.cryptodanilo.project.crypto.presentation.coinDetail

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

enum class ChartTimeframe(
    val label: String,
) {
    ONE_DAY("1D"),
    FIVE_DAYS("5D"),
    ONE_MONTH("1M"),
    SIX_MONTHS("6M"),
    YTD("YTD"),
    ONE_YEAR("1Y"),
    FIVE_YEARS("5Y"),
    ALL("ALL"),
}

// CoinCap's /assets/{id}/history endpoint only supports these intervals — there is
// no weekly interval, so 5Y/ALL fall back to daily and rely on x-axis label
// thinning (see DetailTabContent) to stay readable.
@OptIn(ExperimentalTime::class)
fun ChartTimeframe.toApiParams(now: LocalDateTime): Triple<LocalDateTime, LocalDateTime, String> {
    val timeZone = TimeZone.UTC
    val nowInstant = now.toInstant(timeZone)
    val start =
        when (this) {
            ChartTimeframe.ONE_DAY -> nowInstant.minus(1, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone)
            ChartTimeframe.FIVE_DAYS -> nowInstant.minus(5, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone)
            ChartTimeframe.ONE_MONTH -> nowInstant.minus(1, DateTimeUnit.MONTH, timeZone).toLocalDateTime(timeZone)
            ChartTimeframe.SIX_MONTHS -> nowInstant.minus(6, DateTimeUnit.MONTH, timeZone).toLocalDateTime(timeZone)
            ChartTimeframe.YTD -> LocalDateTime(now.year, 1, 1, 0, 0, 0)
            ChartTimeframe.ONE_YEAR -> nowInstant.minus(1, DateTimeUnit.YEAR, timeZone).toLocalDateTime(timeZone)
            ChartTimeframe.FIVE_YEARS -> nowInstant.minus(5, DateTimeUnit.YEAR, timeZone).toLocalDateTime(timeZone)
            // CoinCap has no data before this; the API simply returns from the
            // coin's earliest available point.
            ChartTimeframe.ALL -> LocalDateTime(2010, 1, 1, 0, 0, 0)
        }
    val interval =
        when (this) {
            ChartTimeframe.ONE_DAY -> "h1"
            ChartTimeframe.FIVE_DAYS -> "h2"
            ChartTimeframe.ONE_MONTH -> "h12"
            ChartTimeframe.SIX_MONTHS, ChartTimeframe.YTD, ChartTimeframe.ONE_YEAR,
            ChartTimeframe.FIVE_YEARS, ChartTimeframe.ALL,
            -> "d1"
        }
    return Triple(start, now, interval)
}

fun ChartTimeframe.formatXAxisLabel(dateTime: LocalDateTime): String =
    when (this) {
        ChartTimeframe.ONE_DAY -> {
            val hour12 = if (dateTime.hour % 12 == 0) 12 else dateTime.hour % 12
            val amPm = if (dateTime.hour < 12) "AM" else "PM"
            "$hour12 $amPm"
        }
        ChartTimeframe.FIVE_DAYS, ChartTimeframe.ONE_MONTH, ChartTimeframe.SIX_MONTHS, ChartTimeframe.YTD ->
            "${dateTime.month.shortName()} ${dateTime.day}"
        ChartTimeframe.ONE_YEAR ->
            "${dateTime.month.shortName()} ${dateTime.year.toString().takeLast(2)}"
        ChartTimeframe.FIVE_YEARS, ChartTimeframe.ALL ->
            dateTime.year.toString()
    }

private fun Month.shortName(): String = name.take(3).lowercase().replaceFirstChar { it.uppercase() }
