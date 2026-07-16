package com.cryptodanilo.project.server.feature.history

import com.cryptodanilo.project.core.Candle

private const val CUBE_VALUE_HOUR = "hour"
private const val CUBE_VALUE_DAY = "day"

private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val MINUTES_PER_HOUR = 60L
private const val HOURS_PER_DAY = 24L
private const val MILLIS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
private const val MILLIS_PER_DAY = HOURS_PER_DAY * MILLIS_PER_HOUR

enum class CandleGranularity(
    val cubeValue: String,
    val bucketMillis: Long,
) {
    HOUR(CUBE_VALUE_HOUR, MILLIS_PER_HOUR),
    DAY(CUBE_VALUE_DAY, MILLIS_PER_DAY),
}

/** Historical candle data for a coin, over the trailing [days], bucketed at
 * [granularity]. [PostgresHistoryDataSource] is the only implementation,
 * reading the Postgres rollup tables directly -- an earlier Cube-backed
 * implementation was removed once it proved too slow/fragile for this
 * fixed six-range use case (see [com.cryptodanilo.project.server.cube.CubeClient],
 * which still exists for a future analytics/chatbot use case, just no
 * longer tied to this interface). */
interface HistoryDataSource {
    suspend fun getCandles(
        coinId: String,
        days: Long,
        granularity: CandleGranularity,
    ): List<Candle>
}
