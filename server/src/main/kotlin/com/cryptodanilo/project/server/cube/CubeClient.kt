package com.cryptodanilo.project.server.cube

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import java.time.Instant

private const val CUBE_LOAD_PATH = "/cubejs-api/v1/load"
private const val AUTHORIZATION_HEADER = "Authorization"
private const val QUERY_PARAM = "query"
private const val JWT_EXPIRY_SECONDS = 60L

/**
 * Thin client for Cube Core's REST API (the semantic layer defined in the
 * backend data platform repo's `cube/model/`) -- handles Cube's own HS256
 * JWT signing (matching `cube/query.sh` in that repo) and the raw
 * `/cubejs-api/v1/load` call, returning Cube's JSON response as-is.
 *
 * Not currently used anywhere in :server -- candle history now reads the
 * Postgres rollup tables directly (see PostgresHistoryDataSource), since
 * querying Cube/BigQuery turned out too slow/fragile for that fixed
 * six-range use case. Kept generic and deliberately un-opinionated about
 * any particular cube/measures for a future analytics/chatbot use case,
 * which is a better fit for Cube's actual strength (ad-hoc analytical
 * querying).
 *
 * Cube's own JWT requirement is unrelated to any app-level auth (there
 * isn't one -- see Application.kt's configureServer doc) -- it's a separate
 * token, signed with Cube's own CUBEJS_API_SECRET.
 */
class CubeClient(
    private val baseUrl: String,
    private val apiSecret: String,
    private val httpClient: HttpClient,
) {
    suspend fun query(cubeQueryJson: String): String {
        val token =
            JWT
                .create()
                .withExpiresAt(Instant.now().plusSeconds(JWT_EXPIRY_SECONDS))
                .sign(Algorithm.HMAC256(apiSecret))

        return httpClient
            .get("$baseUrl$CUBE_LOAD_PATH") {
                header(AUTHORIZATION_HEADER, token)
                parameter(QUERY_PARAM, cubeQueryJson)
            }.bodyAsText()
    }
}
