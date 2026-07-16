package com.cryptodanilo.project.server

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val BITCOIN_MARKETS_PATH = "/api/v1/coins/bitcoin/markets"
private const val UNKNOWN_COIN_MARKETS_PATH = "/api/v1/coins/not-a-real-coin/markets"
private const val BINANCE_JSON_FRAGMENT = "\"binance\""
private const val COIN_NOT_FOUND_CODE = "COIN_NOT_FOUND"

class MarketsRoutesTest {
    @Test
    fun `GET markets for a known coin returns its markets`() =
        testApplication {
            application { testModule() }

            val response = client.get(BITCOIN_MARKETS_PATH)

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains(BINANCE_JSON_FRAGMENT))
        }

    @Test
    fun `GET markets for an unknown coin returns 404`() =
        testApplication {
            application { testModule() }

            val response = client.get(UNKNOWN_COIN_MARKETS_PATH)

            assertEquals(HttpStatusCode.NotFound, response.status)
            assertTrue(response.bodyAsText().contains(COIN_NOT_FOUND_CODE))
        }
}
