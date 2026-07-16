package com.cryptodanilo.project.server

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val BITCOIN_HISTORY_PATH = "/api/v1/coins/bitcoin/history"
private const val UNKNOWN_COIN_HISTORY_PATH = "/api/v1/coins/not-a-real-coin/history"
private const val RANGE_PARAM = "range"
private const val RANGE_1M = "1m"
private const val RANGE_YTD = "ytd"
private const val RANGE_INVALID = "nonsense"
private const val RANGE_1Y = "1y"
private const val BITCOIN_OPEN_JSON_FRAGMENT = "\"open\":65000.0"
private const val INVALID_RANGE_CODE = "INVALID_RANGE"
private const val COIN_NOT_FOUND_CODE = "COIN_NOT_FOUND"
private const val HISTORY_UNAVAILABLE_CODE = "HISTORY_UNAVAILABLE"

class HistoryRoutesTest {
    @Test
    fun `GET history for a known coin returns candles`() =
        testApplication {
            application { testModule() }

            val response = client.get("$BITCOIN_HISTORY_PATH?$RANGE_PARAM=$RANGE_1M")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains(BITCOIN_OPEN_JSON_FRAGMENT))
        }

    @Test
    fun `GET history with the ytd range is accepted`() =
        testApplication {
            application { testModule() }

            val response = client.get("$BITCOIN_HISTORY_PATH?$RANGE_PARAM=$RANGE_YTD")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains(BITCOIN_OPEN_JSON_FRAGMENT))
        }

    @Test
    fun `GET history with an invalid range is rejected`() =
        testApplication {
            application { testModule() }

            val response = client.get("$BITCOIN_HISTORY_PATH?$RANGE_PARAM=$RANGE_INVALID")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains(INVALID_RANGE_CODE))
        }

    @Test
    fun `GET history for an unknown coin returns 404`() =
        testApplication {
            application { testModule() }

            val response = client.get("$UNKNOWN_COIN_HISTORY_PATH?$RANGE_PARAM=$RANGE_1Y")

            assertEquals(HttpStatusCode.NotFound, response.status)
            assertTrue(response.bodyAsText().contains(COIN_NOT_FOUND_CODE))
        }

    @Test
    fun `GET history returns 502 when the data source fails`() =
        testApplication {
            application { testModule(historyDataSource = ThrowingHistoryDataSource()) }

            val response = client.get("$BITCOIN_HISTORY_PATH?$RANGE_PARAM=$RANGE_1Y")

            assertEquals(HttpStatusCode.BadGateway, response.status)
            assertTrue(response.bodyAsText().contains(HISTORY_UNAVAILABLE_CODE))
        }
}
