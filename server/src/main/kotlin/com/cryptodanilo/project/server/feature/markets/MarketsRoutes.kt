package com.cryptodanilo.project.server.feature.markets

import com.cryptodanilo.project.server.common.COIN_NOT_FOUND_CODE
import com.cryptodanilo.project.server.common.ErrorBody
import com.cryptodanilo.project.server.common.ErrorDetail
import com.cryptodanilo.project.server.common.MISSING_COIN_ID_CODE
import com.cryptodanilo.project.server.common.MISSING_COIN_ID_MESSAGE
import com.cryptodanilo.project.server.common.coinNotFoundMessage
import com.cryptodanilo.project.server.common.parseNonNegativeInt
import com.cryptodanilo.project.server.common.parsePositiveInt
import com.cryptodanilo.project.server.common.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

private const val MARKETS_PATH = "/api/v1/coins/{coinId}/markets"
private const val PARAM_COIN_ID = "coinId"
private const val PARAM_LIMIT = "limit"
private const val PARAM_OFFSET = "offset"
private const val DEFAULT_LIMIT = 50
private const val DEFAULT_OFFSET = 0
private const val MAX_LIMIT = 200
private const val INVALID_PAGINATION_CODE = "INVALID_PAGINATION"
private const val INVALID_PAGINATION_MESSAGE = "limit must be 1..$MAX_LIMIT, offset must be >= 0"

fun Route.marketsRoutes(marketsService: MarketsService) {
    get(MARKETS_PATH) {
        val coinId =
            call.parameters[PARAM_COIN_ID] ?: run {
                call.respond(HttpStatusCode.BadRequest, ErrorBody(ErrorDetail(MISSING_COIN_ID_CODE, MISSING_COIN_ID_MESSAGE)))
                return@get
            }
        val limit = call.request.queryParameters.parsePositiveInt(PARAM_LIMIT, DEFAULT_LIMIT, MAX_LIMIT)
        val offset = call.request.queryParameters.parseNonNegativeInt(PARAM_OFFSET, DEFAULT_OFFSET)
        if (limit == null || offset == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorBody(ErrorDetail(INVALID_PAGINATION_CODE, INVALID_PAGINATION_MESSAGE)),
            )
            return@get
        }

        when (val result = marketsService.getMarkets(coinId, limit, offset)) {
            is MarketsResult.CoinNotFound ->
                call.respond(HttpStatusCode.NotFound, ErrorBody(ErrorDetail(COIN_NOT_FOUND_CODE, coinNotFoundMessage(coinId))))
            is MarketsResult.Found ->
                call.respond(result.result.toResponse(limit, offset) { it.toResponse() })
        }
    }
}
