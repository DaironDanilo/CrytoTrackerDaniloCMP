package com.cryptodanilo.project.server

import com.cryptodanilo.project.core.Candle
import com.cryptodanilo.project.server.common.PagedResult
import com.cryptodanilo.project.server.domain.Coin
import com.cryptodanilo.project.server.domain.Market
import com.cryptodanilo.project.server.feature.coins.CoinsRepository
import com.cryptodanilo.project.server.feature.history.CandleGranularity
import com.cryptodanilo.project.server.feature.history.HistoryDataSource
import com.cryptodanilo.project.server.feature.markets.MarketsRepository

private const val BITCOIN_ID = "bitcoin"
private const val BITCOIN_SYMBOL = "btc"
private const val BITCOIN_NAME = "Bitcoin"
private const val BITCOIN_RANK = 1
private const val BITCOIN_PRICE_USD = 65000.0
private const val BITCOIN_MARKET_CAP_USD = 1_290_000_000_000.0
private const val BITCOIN_CHANGE_PERCENT_24H = 2.5

private const val ETHEREUM_ID = "ethereum"
private const val ETHEREUM_SYMBOL = "eth"
private const val ETHEREUM_NAME = "Ethereum"
private const val ETHEREUM_RANK = 2
private const val ETHEREUM_PRICE_USD = 1900.0
private const val ETHEREUM_MARKET_CAP_USD = 230_000_000_000.0
private const val ETHEREUM_CHANGE_PERCENT_24H = -1.2

private const val MARKET_EXCHANGE_ID_BINANCE = "binance"
private const val MARKET_BASE_SYMBOL_BTC = "BTC"
private const val MARKET_TARGET_SYMBOL_USDT = "USDT"
private const val MARKET_PRICE_USD = 65000.0
private const val MARKET_VOLUME_USD_24H = 1_500_000_000.0
private const val MARKET_TRUST_SCORE_HIGH = "high"
private const val MARKET_LAST_TRADED_AT = 1_720_000_000_000L

private const val CANDLE_OPEN_TIME = 1_720_000_000_000L
private const val CANDLE_CLOSE_TIME = 1_720_003_599_999L
private const val CANDLE_OPEN = 65000.0
private const val CANDLE_HIGH = 65200.0
private const val CANDLE_LOW = 64800.0
private const val CANDLE_CLOSE = 65100.0
private const val CANDLE_VOLUME = 120.5
private const val CANDLE_IS_CLOSED = true

private const val SIMULATED_FAILURE_MESSAGE = "simulated data source failure"

class FakeCoinsRepository(
    private val coins: List<Coin> = defaultCoins,
) : CoinsRepository {
    override suspend fun getCoins(
        limit: Int,
        offset: Int,
    ): PagedResult<Coin> =
        PagedResult(
            items = coins.drop(offset).take(limit),
            total = coins.size,
        )

    override suspend fun coinExists(coinId: String): Boolean = coins.any { it.id == coinId }

    companion object {
        val defaultCoins =
            listOf(
                Coin(
                    BITCOIN_ID,
                    BITCOIN_SYMBOL,
                    BITCOIN_NAME,
                    BITCOIN_RANK,
                    BITCOIN_PRICE_USD,
                    BITCOIN_MARKET_CAP_USD,
                    BITCOIN_CHANGE_PERCENT_24H,
                ),
                Coin(
                    ETHEREUM_ID,
                    ETHEREUM_SYMBOL,
                    ETHEREUM_NAME,
                    ETHEREUM_RANK,
                    ETHEREUM_PRICE_USD,
                    ETHEREUM_MARKET_CAP_USD,
                    ETHEREUM_CHANGE_PERCENT_24H,
                ),
            )
    }
}

class FakeMarketsRepository(
    private val marketsByCoin: Map<String, List<Market>> = defaultMarkets,
) : MarketsRepository {
    override suspend fun getMarkets(
        coinId: String,
        limit: Int,
        offset: Int,
    ): PagedResult<Market> {
        val markets = marketsByCoin[coinId].orEmpty()
        return PagedResult(
            items = markets.drop(offset).take(limit),
            total = markets.size,
        )
    }

    companion object {
        val defaultMarkets =
            mapOf(
                BITCOIN_ID to
                    listOf(
                        Market(
                            MARKET_EXCHANGE_ID_BINANCE,
                            MARKET_BASE_SYMBOL_BTC,
                            MARKET_TARGET_SYMBOL_USDT,
                            MARKET_PRICE_USD,
                            MARKET_VOLUME_USD_24H,
                            MARKET_TRUST_SCORE_HIGH,
                            MARKET_LAST_TRADED_AT,
                        ),
                    ),
            )
    }
}

class FakeHistoryDataSource(
    private val candlesByCoin: Map<String, List<Candle>> = defaultCandles,
) : HistoryDataSource {
    override suspend fun getCandles(
        coinId: String,
        days: Long,
        granularity: CandleGranularity,
    ): List<Candle> = candlesByCoin[coinId].orEmpty()

    companion object {
        val defaultCandles =
            mapOf(
                BITCOIN_ID to
                    listOf(
                        Candle(
                            CANDLE_OPEN_TIME,
                            CANDLE_CLOSE_TIME,
                            CANDLE_OPEN,
                            CANDLE_HIGH,
                            CANDLE_LOW,
                            CANDLE_CLOSE,
                            CANDLE_VOLUME,
                            CANDLE_IS_CLOSED,
                        ),
                    ),
            )
    }
}

/** For exercising HistoryRoutes' 502 HISTORY_UNAVAILABLE branch, which
 * [FakeHistoryDataSource] never triggers. */
class ThrowingHistoryDataSource : HistoryDataSource {
    override suspend fun getCandles(
        coinId: String,
        days: Long,
        granularity: CandleGranularity,
    ): List<Candle> = throw RuntimeException(SIMULATED_FAILURE_MESSAGE)
}
