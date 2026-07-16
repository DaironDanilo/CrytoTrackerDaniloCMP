package com.cryptodanilo.project.server.feature.markets

import com.cryptodanilo.project.server.db.Coins
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

private const val TABLE_NAME = "markets"
private const val COLUMN_COIN_ID = "coin_id"
private const val COLUMN_EXCHANGE_ID = "exchange_id"
private const val COLUMN_BASE_SYMBOL = "base_symbol"
private const val COLUMN_TARGET_SYMBOL = "target_symbol"
private const val COLUMN_PRICE_USD = "price_usd"
private const val COLUMN_VOLUME_USD_24H = "volume_usd_24h"
private const val COLUMN_TRUST_SCORE = "trust_score"
private const val COLUMN_LAST_TRADED_AT = "last_traded_at"
private const val COLUMN_UPDATED_AT = "updated_at"

// decimal(precision, scale): precision = total significant digits, scale = digits after the decimal point
private const val PRICE_USD_PRECISION = 24
private const val PRICE_USD_SCALE = 10
private const val VOLUME_USD_24H_PRECISION = 28
private const val VOLUME_USD_24H_SCALE = 2

object Markets : Table(TABLE_NAME) {
    val coinId = text(COLUMN_COIN_ID).references(Coins.coinId)
    val exchangeId = text(COLUMN_EXCHANGE_ID)
    val baseSymbol = text(COLUMN_BASE_SYMBOL)
    val targetSymbol = text(COLUMN_TARGET_SYMBOL)
    val priceUsd = decimal(COLUMN_PRICE_USD, PRICE_USD_PRECISION, PRICE_USD_SCALE).nullable()
    val volumeUsd24h = decimal(COLUMN_VOLUME_USD_24H, VOLUME_USD_24H_PRECISION, VOLUME_USD_24H_SCALE).nullable()
    val trustScore = text(COLUMN_TRUST_SCORE).nullable()
    val lastTradedAt = timestamp(COLUMN_LAST_TRADED_AT).nullable()
    val updatedAt = timestamp(COLUMN_UPDATED_AT)
    override val primaryKey = PrimaryKey(coinId, exchangeId, baseSymbol, targetSymbol)
}
