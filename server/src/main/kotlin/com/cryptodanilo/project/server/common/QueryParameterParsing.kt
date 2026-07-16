package com.cryptodanilo.project.server.common

import io.ktor.http.Parameters

/** Shared HTTP query-parameter parsing, used by every paginated route --
 * pulled into its own file so that dependency is explicit rather than
 * relying on same-package `internal` visibility across route files. */
fun Parameters.parsePositiveInt(
    name: String,
    default: Int,
    max: Int,
): Int? {
    val raw = this[name] ?: return default
    val value = raw.toIntOrNull() ?: return null
    return if (value in 1..max) value else null
}

fun Parameters.parseNonNegativeInt(
    name: String,
    default: Int,
): Int? {
    val raw = this[name] ?: return default
    val value = raw.toIntOrNull() ?: return null
    return if (value >= 0) value else null
}
