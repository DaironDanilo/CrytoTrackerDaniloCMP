package com.cryptodanilo.project.server.feature.coins

import com.cryptodanilo.project.server.common.PagedResult
import com.cryptodanilo.project.server.db.Coins
import com.cryptodanilo.project.server.domain.Coin
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface CoinsRepository {
    suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): PagedResult<Coin>

    suspend fun coinExists(coinId: String): Boolean
}

class ExposedCoinsRepository : CoinsRepository {
    override suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): PagedResult<Coin> =
        newSuspendedTransaction {
            // binanceSymbol IS NOT NULL: the entire candle pipeline is
            // Binance-kline-sourced, so a coin with no Binance USDT pair (a
            // stablecoin can't trade against itself; plenty of ordinary
            // altcoins simply aren't listed against USDT on Binance either)
            // would show in the list with a permanently empty chart on
            // selection. Filtering it out of the list here is simpler and
            // more correct than the client having to special-case an asset
            // it was never going to be able to show anything useful for.
            val total =
                (Coins innerJoin CoinSnapshots)
                    .selectAll()
                    .where { Coins.binanceSymbol.isNotNull() }
                    .count()
                    .toInt()
            val rows =
                (Coins innerJoin CoinSnapshots)
                    .selectAll()
                    .where { Coins.binanceSymbol.isNotNull() }
                    .orderBy(CoinSnapshots.rank to SortOrder.ASC_NULLS_LAST)
                    .limit(limit, offset.toLong())
                    .map { row ->
                        Coin(
                            id = row[Coins.coinId],
                            symbol = row[Coins.symbol],
                            name = row[Coins.name],
                            rank = row[CoinSnapshots.rank],
                            priceUsd = row[CoinSnapshots.priceUsd].toDouble(),
                            marketCapUsd = row[CoinSnapshots.marketCapUsd]?.toDouble(),
                            changePercent24h = row[CoinSnapshots.changePercent24h]?.toDouble(),
                        )
                    }
            PagedResult(rows, total)
        }

    override suspend fun coinExists(coinId: String): Boolean =
        newSuspendedTransaction {
            Coins.selectAll().where { Coins.coinId eq coinId }.count() > 0
        }
}
