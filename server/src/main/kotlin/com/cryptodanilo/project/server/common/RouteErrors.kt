package com.cryptodanilo.project.server.common

/** Error codes/messages duplicated verbatim across feature routes (both
 * markets and history need "coinId path segment is required" and
 * "COIN_NOT_FOUND" identically) -- shared here instead of repeating the
 * literal in each route file. */
const val MISSING_COIN_ID_CODE = "MISSING_COIN_ID"
const val MISSING_COIN_ID_MESSAGE = "coinId path segment is required"
const val COIN_NOT_FOUND_CODE = "COIN_NOT_FOUND"

fun coinNotFoundMessage(coinId: String): String = "No coin with id '$coinId'"
