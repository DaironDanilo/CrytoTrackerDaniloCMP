package com.cryptodanilo.project.server

import com.cryptodanilo.project.server.common.ErrorBody
import com.cryptodanilo.project.server.common.ErrorDetail
import com.cryptodanilo.project.server.db.DatabaseFactory
import com.cryptodanilo.project.server.feature.coins.CoinsRepository
import com.cryptodanilo.project.server.feature.coins.CoinsService
import com.cryptodanilo.project.server.feature.coins.ExposedCoinsRepository
import com.cryptodanilo.project.server.feature.coins.coinsRoutes
import com.cryptodanilo.project.server.feature.history.HistoryDataSource
import com.cryptodanilo.project.server.feature.history.HistoryService
import com.cryptodanilo.project.server.feature.history.PostgresHistoryDataSource
import com.cryptodanilo.project.server.feature.history.historyRoutes
import com.cryptodanilo.project.server.feature.markets.ExposedMarketsRepository
import com.cryptodanilo.project.server.feature.markets.MarketsRepository
import com.cryptodanilo.project.server.feature.markets.MarketsService
import com.cryptodanilo.project.server.feature.markets.marketsRoutes
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.slf4j.event.Level

private const val ENV_DATABASE_URL = "DATABASE_URL"
private const val ENV_PORT = "PORT"
private const val DEFAULT_PORT = 8080

private const val INTERNAL_ERROR_CODE = "INTERNAL_ERROR"
private const val INTERNAL_ERROR_MESSAGE = "Something went wrong"
private const val UNHANDLED_EXCEPTION_LOG_MESSAGE = "Unhandled exception"

private fun requiredEnv(name: String): String = System.getenv(name) ?: error("$name environment variable is required")

fun main() {
    val port = System.getenv(ENV_PORT)?.toIntOrNull() ?: DEFAULT_PORT
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    val databaseUrl = requiredEnv(ENV_DATABASE_URL)
    DatabaseFactory.init(databaseUrl)

    val coinsRepository = ExposedCoinsRepository()
    configureServer(
        coinsRepository = coinsRepository,
        marketsRepository = ExposedMarketsRepository(),
        // Reads the candle_rollups_hourly/daily tables kept fresh by
        // candle-hourly-sync-job / candle-daily-sync-job (backend data
        // platform repo) instead of Cube/BigQuery, which was too
        // slow/fragile for this endpoint. Cube itself is untouched
        // (CubeClient still exists, kept for a future analytics/chatbot use
        // case), just no longer wired into this request path.
        historyDataSource = PostgresHistoryDataSource(),
    )
}

/**
 * The actual plugin/routing wiring, factored out from [module] so tests can
 * call it directly with fake repositories/data source instead of standing
 * up a real Postgres connection.
 *
 * No authentication: the app has no login flow yet, so it talks to this
 * backend directly. Add auth here once the client has a real account system.
 */
fun Application.configureServer(
    coinsRepository: CoinsRepository,
    marketsRepository: MarketsRepository,
    historyDataSource: HistoryDataSource,
) {
    install(ContentNegotiation) { json() }
    install(CallLogging) { level = Level.INFO }
    // Needed for the web (wasmJs) target: it runs in a real browser, which
    // enforces CORS on cross-origin fetch() calls (the other targets --
    // Android/iOS/Desktop -- don't go through a browser fetch sandbox, so
    // this was invisible until the web target was actually run). No
    // credentials/cookies are involved (no auth yet), so anyHost() is safe
    // here -- revisit if that changes.
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
        // The web target's shared HttpClient (HttpClientFactory.kt) attaches
        // an Authorization header to every request via defaultRequest --
        // including calls here -- left over from the CoinCap client config.
        // This server ignores it, but the browser still puts it in the
        // preflight's requested-headers list, so it has to be allowed here
        // or the whole preflight (and therefore the real request) is
        // rejected. Removing that stray header client-side would be the
        // more correct fix long-term; allowing it here is the pragmatic one.
        allowHeader(HttpHeaders.Authorization)
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log
                .error(UNHANDLED_EXCEPTION_LOG_MESSAGE, cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorBody(ErrorDetail(INTERNAL_ERROR_CODE, INTERNAL_ERROR_MESSAGE)))
        }
    }

    val coinsService = CoinsService(coinsRepository)
    val marketsService = MarketsService(marketsRepository, coinsRepository)
    val historyService = HistoryService(historyDataSource, coinsRepository)

    routing {
        coinsRoutes(coinsService)
        marketsRoutes(marketsService)
        historyRoutes(historyService)
    }
}
