package com.cryptodanilo.project.server.feature.history

import com.cryptodanilo.project.core.Candle
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Reads candle history from the Postgres rollup tables kept fresh by
 * candle-hourly-sync-job / candle-daily-sync-job (backend data platform
 * repo), instead of Cube/BigQuery, which turned out too slow/fragile for
 * this endpoint. Cube itself is untouched and still available (CubeClient,
 * kept for a future analytics/chatbot use case, no longer implements
 * [HistoryDataSource]); this is the only implementation of that interface now.
 *
 * 1D/5D read [CandleRollupsHourly] directly (already refreshed every 5
 * min). 1M/6M/YTD/1Y read [CandleRollupsDaily] for everything except today
 * (that table only refreshes once a day) and merge in a synthetic "today"
 * candle computed on the fly from today's [CandleRollupsHourly] rows, so
 * the long ranges still feel fresh between daily-sync runs -- this is the
 * Lambda architecture's batch-layer/speed-layer merge.
 */
class PostgresHistoryDataSource : HistoryDataSource {
    override suspend fun getCandles(
        coinId: String,
        days: Long,
        granularity: CandleGranularity,
    ): List<Candle> =
        newSuspendedTransaction {
            when (granularity) {
                CandleGranularity.HOUR -> hourlyCandles(coinId, days)
                CandleGranularity.DAY -> dailyCandlesWithTodayMerged(coinId, days)
            }
        }

    private fun hourlyCandles(
        coinId: String,
        days: Long,
    ): List<Candle> {
        val since = Instant.now().minus(Duration.ofDays(days))
        return CandleRollupsHourly
            .selectAll()
            .where { CandleRollupsHourly.coinId eq coinId }
            .andWhere { CandleRollupsHourly.bucketStart greaterEq since }
            .orderBy(CandleRollupsHourly.bucketStart to SortOrder.ASC)
            .map { it.toHourlyCandle() }
    }

    private fun dailyCandlesWithTodayMerged(
        coinId: String,
        days: Long,
    ): List<Candle> {
        val now = Instant.now()
        val startOfToday = now.truncatedTo(ChronoUnit.DAYS)
        val since = now.minus(Duration.ofDays(days))

        val historical =
            CandleRollupsDaily
                .selectAll()
                .where { CandleRollupsDaily.coinId eq coinId }
                .andWhere { CandleRollupsDaily.bucketStart greaterEq since }
                .andWhere { CandleRollupsDaily.bucketStart less startOfToday }
                .orderBy(CandleRollupsDaily.bucketStart to SortOrder.ASC)
                .map { it.toDailyCandle() }

        val todayHours =
            CandleRollupsHourly
                .selectAll()
                .where { CandleRollupsHourly.coinId eq coinId }
                .andWhere { CandleRollupsHourly.bucketStart greaterEq startOfToday }
                .orderBy(CandleRollupsHourly.bucketStart to SortOrder.ASC)
                .toList()

        val today = todayHours.toSyntheticDailyCandle(startOfToday)
        return if (today != null) historical + today else historical
    }

    private fun ResultRow.toHourlyCandle(): Candle {
        val openTime = this[CandleRollupsHourly.bucketStart].toEpochMilli()
        return Candle(
            openTime = openTime,
            closeTime = openTime + HOUR_MILLIS - BUCKET_END_INCLUSIVE_OFFSET_MILLIS,
            open = this[CandleRollupsHourly.open].toDouble(),
            high = this[CandleRollupsHourly.high].toDouble(),
            low = this[CandleRollupsHourly.low].toDouble(),
            close = this[CandleRollupsHourly.close].toDouble(),
            volume = this[CandleRollupsHourly.volume].toDouble(),
            isClosed = this[CandleRollupsHourly.isClosed],
        )
    }

    private fun ResultRow.toDailyCandle(): Candle {
        val openTime = this[CandleRollupsDaily.bucketStart].toEpochMilli()
        return Candle(
            openTime = openTime,
            closeTime = openTime + DAY_MILLIS - BUCKET_END_INCLUSIVE_OFFSET_MILLIS,
            open = this[CandleRollupsDaily.open].toDouble(),
            high = this[CandleRollupsDaily.high].toDouble(),
            low = this[CandleRollupsDaily.low].toDouble(),
            close = this[CandleRollupsDaily.close].toDouble(),
            volume = this[CandleRollupsDaily.volume].toDouble(),
            isClosed = this[CandleRollupsDaily.isClosed],
        )
    }

    private fun List<ResultRow>.toSyntheticDailyCandle(bucketStart: Instant): Candle? {
        if (isEmpty()) return null
        val sortedByTime = sortedBy { it[CandleRollupsHourly.bucketStart] }
        return Candle(
            openTime = bucketStart.toEpochMilli(),
            closeTime = bucketStart.toEpochMilli() + DAY_MILLIS - BUCKET_END_INCLUSIVE_OFFSET_MILLIS,
            open = sortedByTime.first()[CandleRollupsHourly.open].toDouble(),
            high = maxOf { it[CandleRollupsHourly.high].toDouble() },
            low = minOf { it[CandleRollupsHourly.low].toDouble() },
            close = sortedByTime.last()[CandleRollupsHourly.close].toDouble(),
            volume = sumOf { it[CandleRollupsHourly.volume].toDouble() },
            // "today" is by definition still forming -- never fully closed.
            isClosed = false,
        )
    }

    private companion object {
        private const val MILLIS_PER_SECOND = 1000L
        private const val SECONDS_PER_MINUTE = 60L
        private const val MINUTES_PER_HOUR = 60L
        private const val HOURS_PER_DAY = 24L

        const val HOUR_MILLIS = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND
        const val DAY_MILLIS = HOURS_PER_DAY * HOUR_MILLIS

        // closeTime is inclusive, so it's one millisecond before the next bucket starts.
        const val BUCKET_END_INCLUSIVE_OFFSET_MILLIS = 1L
    }
}
