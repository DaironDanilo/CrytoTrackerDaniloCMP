package com.cryptodanilo.project.server

import com.cryptodanilo.project.server.feature.coins.CoinsRepository
import com.cryptodanilo.project.server.feature.history.HistoryDataSource
import com.cryptodanilo.project.server.feature.markets.MarketsRepository
import io.ktor.server.application.Application

fun Application.testModule(
    coinsRepository: CoinsRepository = FakeCoinsRepository(),
    marketsRepository: MarketsRepository = FakeMarketsRepository(),
    historyDataSource: HistoryDataSource = FakeHistoryDataSource(),
) {
    configureServer(
        coinsRepository = coinsRepository,
        marketsRepository = marketsRepository,
        historyDataSource = historyDataSource,
    )
}
