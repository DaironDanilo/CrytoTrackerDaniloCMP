package com.cryptodanilo.project.server

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val COINS_PATH = "/api/v1/coins"
private const val INVALID_PAGINATION_CODE = "INVALID_PAGINATION"

private const val PARAM_LIMIT = "limit"
private const val PARAM_OFFSET = "offset"
private const val LIMIT_ONE = 1
private const val OFFSET_ONE = 1
private const val LIMIT_NON_NUMERIC = "abc"
private const val LIMIT_ZERO = 0
private const val LIMIT_OVER_MAX = 201
private const val OFFSET_NEGATIVE = -1

private const val BITCOIN_JSON_FRAGMENT = "\"bitcoin\""
private const val ETHEREUM_JSON_FRAGMENT = "\"ethereum\""
private const val TOTAL_TWO_JSON_FRAGMENT = "\"total\":2"
private const val HAS_MORE_FALSE_JSON_FRAGMENT = "\"hasMore\":false"

class CoinsRoutesTest {
    @Test
    fun `GET coins returns paginated coin list`() =
        testApplication {
            application { testModule() }

            val response = client.get(COINS_PATH)

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains(BITCOIN_JSON_FRAGMENT))
            assertTrue(body.contains(TOTAL_TWO_JSON_FRAGMENT))
        }

    @Test
    fun `GET coins with offset returns the next page`() =
        testApplication {
            application { testModule() }

            val response = client.get("$COINS_PATH?$PARAM_LIMIT=$LIMIT_ONE&$PARAM_OFFSET=$OFFSET_ONE")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = response.bodyAsText()
            assertTrue(body.contains(ETHEREUM_JSON_FRAGMENT))
            assertTrue(!body.contains(BITCOIN_JSON_FRAGMENT))
            assertTrue(body.contains(HAS_MORE_FALSE_JSON_FRAGMENT))
        }

    @Test
    fun `GET coins with a non-numeric limit is rejected`() =
        testApplication {
            application { testModule() }

            val response = client.get("$COINS_PATH?$PARAM_LIMIT=$LIMIT_NON_NUMERIC")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains(INVALID_PAGINATION_CODE))
        }

    @Test
    fun `GET coins with a zero limit is rejected`() =
        testApplication {
            application { testModule() }

            val response = client.get("$COINS_PATH?$PARAM_LIMIT=$LIMIT_ZERO")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains(INVALID_PAGINATION_CODE))
        }

    @Test
    fun `GET coins with a limit over the max is rejected`() =
        testApplication {
            application { testModule() }

            val response = client.get("$COINS_PATH?$PARAM_LIMIT=$LIMIT_OVER_MAX")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains(INVALID_PAGINATION_CODE))
        }

    @Test
    fun `GET coins with a negative offset is rejected`() =
        testApplication {
            application { testModule() }

            val response = client.get("$COINS_PATH?$PARAM_OFFSET=$OFFSET_NEGATIVE")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains(INVALID_PAGINATION_CODE))
        }
}
