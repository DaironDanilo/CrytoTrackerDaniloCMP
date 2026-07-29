package com.cryptodanilo.project.server.feature.history

import com.cryptodanilo.project.server.db.Coins
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

private const val TABLE_NAME_HOURLY = "candle_rollups_hourly"
private const val TABLE_NAME_DAILY = "candle_rollups_daily"

private const val COLUMN_COIN_ID = "coin_id"
private const val COLUMN_BUCKET_START = "bucket_start"
private const val COLUMN_OPEN = "open"
private const val COLUMN_HIGH = "high"
private const val COLUMN_LOW = "low"
private const val COLUMN_CLOSE = "close"
private const val COLUMN_VOLUME = "volume"
private const val COLUMN_IS_CLOSED = "is_closed"

// decimal(precision, scale): precision = total significant digits, scale = digits after the decimal point
private const val PRICE_PRECISION = 24
private const val PRICE_SCALE = 10
private const val VOLUME_PRECISION = 28
private const val VOLUME_SCALE = 10

/** Lambda-architecture "speed layer" -- short retention (10 days), refreshed
 * every 5 minutes by candle-hourly-sync-job (backend data platform repo)
 * from BigQuery's gold.hourly_candle_metrics. Serves the 1D/5D chart ranges
 * directly, and supplies "today"'s rows for synthesizing a fresh daily
 * candle when serving the longer ranges (see PostgresHistoryDataSource). */
object CandleRollupsHourly : Table(TABLE_NAME_HOURLY) {
    val coinId = text(COLUMN_COIN_ID).references(Coins.coinId)
    val bucketStart = timestamp(COLUMN_BUCKET_START)
    val open = decimal(COLUMN_OPEN, PRICE_PRECISION, PRICE_SCALE)
    val high = decimal(COLUMN_HIGH, PRICE_PRECISION, PRICE_SCALE)
    val low = decimal(COLUMN_LOW, PRICE_PRECISION, PRICE_SCALE)
    val close = decimal(COLUMN_CLOSE, PRICE_PRECISION, PRICE_SCALE)
    val volume = decimal(COLUMN_VOLUME, VOLUME_PRECISION, VOLUME_SCALE)
    val isClosed = bool(COLUMN_IS_CLOSED)
    override val primaryKey = PrimaryKey(coinId, bucketStart)
}

/** Lambda-architecture "batch layer" -- 13-month retention (matches the
 * app's max 1Y range), refreshed once daily by candle-daily-sync-job from
 * BigQuery's gold.daily_candle_metrics. Serves the 1M/6M/YTD/1Y chart
 * ranges, with today's bucket always excluded here and synthesized fresh
 * from CandleRollupsHourly instead (see PostgresHistoryDataSource) since
 * this table only updates once a day. */
object CandleRollupsDaily : Table(TABLE_NAME_DAILY) {
    val coinId = text(COLUMN_COIN_ID).references(Coins.coinId)
    val bucketStart = timestamp(COLUMN_BUCKET_START)
    val open = decimal(COLUMN_OPEN, PRICE_PRECISION, PRICE_SCALE)
    val high = decimal(COLUMN_HIGH, PRICE_PRECISION, PRICE_SCALE)
    val low = decimal(COLUMN_LOW, PRICE_PRECISION, PRICE_SCALE)
    val close = decimal(COLUMN_CLOSE, PRICE_PRECISION, PRICE_SCALE)
    val volume = decimal(COLUMN_VOLUME, VOLUME_PRECISION, VOLUME_SCALE)
    val isClosed = bool(COLUMN_IS_CLOSED)
    override val primaryKey = PrimaryKey(coinId, bucketStart)
}
