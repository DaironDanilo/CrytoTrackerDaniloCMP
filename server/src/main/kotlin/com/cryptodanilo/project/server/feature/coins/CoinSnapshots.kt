package com.cryptodanilo.project.server.feature.coins

import com.cryptodanilo.project.server.db.Coins
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

private const val TABLE_NAME = "coin_snapshots"
private const val COLUMN_COIN_ID = "coin_id"
private const val COLUMN_PRICE_USD = "price_usd"
private const val COLUMN_MARKET_CAP_USD = "market_cap_usd"
private const val COLUMN_RANK = "rank"
private const val COLUMN_CHANGE_PERCENT_24H = "change_percent_24h"
private const val COLUMN_UPDATED_AT = "updated_at"
private const val COLUMN_PRICE_SOURCE = "price_source"

// decimal(precision, scale): precision = total significant digits, scale = digits after the decimal point
private const val PRICE_USD_PRECISION = 24
private const val PRICE_USD_SCALE = 10
private const val MARKET_CAP_USD_PRECISION = 24
private const val MARKET_CAP_USD_SCALE = 2
private const val CHANGE_PERCENT_24H_PRECISION = 10
private const val CHANGE_PERCENT_24H_SCALE = 4

object CoinSnapshots : Table(TABLE_NAME) {
    val coinId = text(COLUMN_COIN_ID).references(Coins.coinId)
    val priceUsd = decimal(COLUMN_PRICE_USD, PRICE_USD_PRECISION, PRICE_USD_SCALE)
    val marketCapUsd = decimal(COLUMN_MARKET_CAP_USD, MARKET_CAP_USD_PRECISION, MARKET_CAP_USD_SCALE).nullable()
    val rank = integer(COLUMN_RANK).nullable()
    val changePercent24h = decimal(COLUMN_CHANGE_PERCENT_24H, CHANGE_PERCENT_24H_PRECISION, CHANGE_PERCENT_24H_SCALE).nullable()
    val updatedAt = timestamp(COLUMN_UPDATED_AT)
    val priceSource = text(COLUMN_PRICE_SOURCE).nullable()
    override val primaryKey = PrimaryKey(coinId)
}
