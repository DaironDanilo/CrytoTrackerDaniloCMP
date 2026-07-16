package com.cryptodanilo.project.server.common

data class PagedResult<T>(
    val items: List<T>,
    val total: Int,
)
