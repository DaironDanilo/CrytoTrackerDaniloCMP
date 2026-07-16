package com.cryptodanilo.project.server

import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

private const val COINS_PATH = "/api/v1/coins"
private const val TEST_ORIGIN = "http://localhost:8080"
private const val REQUEST_METHOD_GET = "GET"
private const val REQUESTED_HEADERS = "authorization,content-type"
private const val AUTHORIZATION_HEADER_NAME = "Authorization"

/** Regression test for a real bug: the web (wasmJs) client's shared
 * HttpClient attaches an Authorization header to every request (a leftover
 * from the CoinCap client config), which browsers include in the CORS
 * preflight's requested-headers list. If this server doesn't explicitly
 * allow that header, the preflight -- and therefore the real request --
 * gets rejected. This was only caught by testing the actual deployed web
 * target manually; this test exists so it can't regress silently again. */
class CorsTest {
    @Test
    fun `preflight requesting the Authorization header succeeds`() =
        testApplication {
            application { testModule() }

            val response =
                client.request(COINS_PATH) {
                    method = HttpMethod.Options
                    header(HttpHeaders.Origin, TEST_ORIGIN)
                    header(HttpHeaders.AccessControlRequestMethod, REQUEST_METHOD_GET)
                    header(HttpHeaders.AccessControlRequestHeaders, REQUESTED_HEADERS)
                }

            assertEquals(HttpStatusCode.OK, response.status)
            val allowedHeaders = response.headers[HttpHeaders.AccessControlAllowHeaders].orEmpty()
            assertEquals(true, allowedHeaders.contains(AUTHORIZATION_HEADER_NAME))
        }
}
