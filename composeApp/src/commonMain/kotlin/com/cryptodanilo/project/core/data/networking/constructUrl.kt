package com.cryptodanilo.project.core.data.networking

fun constructUrl(
    url: String,
): String {
    val baseUrl = "https://api.coincap.io/v2/"
    return when {
        url.contains(baseUrl) -> url
        url.startsWith("/") -> baseUrl + url.drop(1)
        else -> baseUrl + url
    }
}
