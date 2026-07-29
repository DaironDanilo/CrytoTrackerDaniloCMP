package com.cryptodanilo.project.server.db

import org.jetbrains.exposed.v1.core.Table

private const val TABLE_NAME = "coins"
private const val COLUMN_COIN_ID = "coin_id"
private const val COLUMN_SYMBOL = "symbol"
private const val COLUMN_NAME = "name"
private const val COLUMN_BINANCE_SYMBOL = "binance_symbol"

/** Mirrors the `coins` table already managed by the backend data platform's
 * own Supabase migrations (coin-registry/migrations/0001_init.sql). This
 * server only reads it -- schema ownership stays with that repo.
 *
 * Lives here (not under feature/coins) because it's genuinely cross-feature:
 * markets and history both reference it too (coin-existence checks, FKs). */
object Coins : Table(TABLE_NAME) {
    val coinId = text(COLUMN_COIN_ID)
    val symbol = text(COLUMN_SYMBOL)
    val name = text(COLUMN_NAME)
    val binanceSymbol = text(COLUMN_BINANCE_SYMBOL).nullable()
    override val primaryKey = PrimaryKey(coinId)
}
