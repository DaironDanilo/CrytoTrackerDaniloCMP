package com.cryptodanilo.project.server.feature.history

import com.cryptodanilo.project.core.Candle
import com.cryptodanilo.project.server.feature.coins.CoinsRepository
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

private const val RANGE_YTD = "ytd"
private const val RANGE_1D = "1d"
private const val RANGE_5D = "5d"
private const val RANGE_1M = "1m"
private const val RANGE_6M = "6m"
private const val RANGE_1Y = "1y"

private const val DAYS_1D = 1L
private const val DAYS_5D = 5L
private const val DAYS_1M = 30L
private const val DAYS_6M = 182L
private const val DAYS_1Y = 365L
private const val MIN_YTD_DAYS = 1L

private const val JANUARY = 1
private const val FIRST_DAY_OF_MONTH = 1

sealed interface HistoryResult {
    data class Found(
        val candles: List<Candle>,
    ) : HistoryResult

    data object CoinNotFound : HistoryResult

    data object InvalidRange : HistoryResult

    data class Unavailable(
        val cause: Throwable,
    ) : HistoryResult
}

/**
 * Owns everything that used to live inline in the route handler: resolving
 * the app's six range codes (1d/5d/1m/6m/ytd/1y -- mirrors
 * ChartTimeframe.label lowercased, see the CMP client) into a lookback
 * window + granularity, the coin-existence check, and translating a failed
 * data-source call into a typed result instead of a raw exception.
 */
class HistoryService(
    private val historyDataSource: HistoryDataSource,
    private val coinsRepository: CoinsRepository,
) {
    suspend fun getHistory(
        coinId: String,
        rangeParam: String,
    ): HistoryResult {
        val resolved = resolveRange(rangeParam) ?: return HistoryResult.InvalidRange
        if (!coinsRepository.coinExists(coinId)) return HistoryResult.CoinNotFound

        return try {
            val (days, granularity) = resolved
            HistoryResult.Found(historyDataSource.getCandles(coinId, days, granularity))
        } catch (e: Exception) {
            HistoryResult.Unavailable(e)
        }
    }

    private fun resolveRange(rangeParam: String): Pair<Long, CandleGranularity>? =
        when {
            rangeParam == RANGE_YTD -> {
                val today = LocalDate.now(ZoneOffset.UTC)
                val jan1 = LocalDate.of(today.year, JANUARY, FIRST_DAY_OF_MONTH)
                ChronoUnit.DAYS.between(jan1, today).coerceAtLeast(MIN_YTD_DAYS) to CandleGranularity.DAY
            }
            rangeParam in FIXED_RANGES -> FIXED_RANGES.getValue(rangeParam)
            else -> null
        }

    companion object {
        const val DEFAULT_RANGE = RANGE_1Y

        // Mirrors the app's ChartTimeframe options exactly (1D/5D/1M/6M/YTD/1Y,
        // lowercased) so the client can pass its selected tab straight through
        // as the `range` param with no translation on either side. Granularity
        // is picked per range so the payload stays small: intraday ranges use
        // hourly candles, anything a month or longer uses daily -- a chart
        // doesn't look any smoother with more points than that, it just costs
        // more to transfer and render.
        private val FIXED_RANGES =
            mapOf(
                RANGE_1D to (DAYS_1D to CandleGranularity.HOUR),
                RANGE_5D to (DAYS_5D to CandleGranularity.HOUR),
                RANGE_1M to (DAYS_1M to CandleGranularity.DAY),
                RANGE_6M to (DAYS_6M to CandleGranularity.DAY),
                RANGE_1Y to (DAYS_1Y to CandleGranularity.DAY),
            )
        val VALID_RANGES = FIXED_RANGES.keys + RANGE_YTD
    }
}
