package com.cryptodanilo.project.server.feature.history

import com.cryptodanilo.project.server.common.COIN_NOT_FOUND_CODE
import com.cryptodanilo.project.server.common.ErrorBody
import com.cryptodanilo.project.server.common.ErrorDetail
import com.cryptodanilo.project.server.common.MISSING_COIN_ID_CODE
import com.cryptodanilo.project.server.common.MISSING_COIN_ID_MESSAGE
import com.cryptodanilo.project.server.common.coinNotFoundMessage
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("HistoryRoutes")

private const val HISTORY_PATH = "/api/v1/coins/{coinId}/history"
private const val PARAM_COIN_ID = "coinId"
private const val PARAM_RANGE = "range"
private const val INVALID_RANGE_CODE = "INVALID_RANGE"
private const val HISTORY_UNAVAILABLE_CODE = "HISTORY_UNAVAILABLE"
private const val HISTORY_UNAVAILABLE_MESSAGE = "Historical data source is unavailable"
private const val HISTORY_REQUEST_FAILED_LOG_PREFIX = "History request failed"
private const val INVALID_RANGE_MESSAGE_PREFIX = "range must be one of: "

private fun invalidRangeMessage(): String = "$INVALID_RANGE_MESSAGE_PREFIX${HistoryService.VALID_RANGES.joinToString()}"

fun Route.historyRoutes(historyService: HistoryService) {
    get(HISTORY_PATH) {
        val coinId =
            call.parameters[PARAM_COIN_ID] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorBody(ErrorDetail(MISSING_COIN_ID_CODE, MISSING_COIN_ID_MESSAGE)))
                return@get
            }
        val rangeParam = call.request.queryParameters[PARAM_RANGE] ?: HistoryService.DEFAULT_RANGE

        when (val result = historyService.getHistory(coinId, rangeParam)) {
            is HistoryResult.InvalidRange ->
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorBody(ErrorDetail(INVALID_RANGE_CODE, invalidRangeMessage())),
                )
            is HistoryResult.CoinNotFound ->
                call.respond(HttpStatusCode.NotFound, ErrorBody(ErrorDetail(COIN_NOT_FOUND_CODE, coinNotFoundMessage(coinId))))
            is HistoryResult.Unavailable -> {
                logger.error("$HISTORY_REQUEST_FAILED_LOG_PREFIX for coinId=$coinId range=$rangeParam", result.cause)
                call.respond(
                    HttpStatusCode.BadGateway,
                    ErrorBody(ErrorDetail(HISTORY_UNAVAILABLE_CODE, HISTORY_UNAVAILABLE_MESSAGE)),
                )
            }
            is HistoryResult.Found -> call.respond(result.candles)
        }
    }
}
