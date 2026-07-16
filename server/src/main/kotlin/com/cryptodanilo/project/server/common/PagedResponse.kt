package com.cryptodanilo.project.server.common

import kotlinx.serialization.Serializable

@Serializable
data class PageInfo(
    val limit: Int,
    val offset: Int,
    val total: Int,
    val hasMore: Boolean,
)

@Serializable
data class PagedResponse<T>(
    val data: List<T>,
    val page: PageInfo,
)

fun <T, R> PagedResult<T>.toResponse(
    limit: Int,
    offset: Int,
    mapper: (T) -> R,
): PagedResponse<R> =
    PagedResponse(
        data = items.map(mapper),
        page = PageInfo(limit, offset, total, hasMore = offset + items.size < total),
    )
