package com.cryptodanilo.project.server.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorBody(
    val error: ErrorDetail,
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String,
)
