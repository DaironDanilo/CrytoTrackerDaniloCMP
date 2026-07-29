package com.cryptodanilo.project.server.feature.markets

import com.cryptodanilo.project.server.common.PagedResult
import com.cryptodanilo.project.server.domain.Market
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

interface MarketsRepository {
    suspend fun getMarkets(
        coinId: String,
        limit: Int,
        offset: Int,
    ): PagedResult<Market>
}

class ExposedMarketsRepository : MarketsRepository {
    override suspend fun getMarkets(
        coinId: String,
        limit: Int,
        offset: Int,
    ): PagedResult<Market> =
        newSuspendedTransaction {
            val total =
                Markets
                    .selectAll()
                    .where { Markets.coinId eq coinId }
                    .count()
                    .toInt()
            val rows =
                Markets
                    .selectAll()
                    .where { Markets.coinId eq coinId }
                    .orderBy(Markets.volumeUsd24h to SortOrder.DESC_NULLS_LAST)
                    .limit(limit)
                    .offset(offset.toLong())
                    .map { row ->
                        Market(
                            exchangeId = row[Markets.exchangeId],
                            baseSymbol = row[Markets.baseSymbol],
                            targetSymbol = row[Markets.targetSymbol],
                            priceUsd = row[Markets.priceUsd]?.toDouble(),
                            volumeUsd24h = row[Markets.volumeUsd24h]?.toDouble(),
                            trustScore = row[Markets.trustScore],
                            lastTradedAt = row[Markets.lastTradedAt]?.toEpochMilli(),
                        )
                    }
            PagedResult(rows, total)
        }
}
