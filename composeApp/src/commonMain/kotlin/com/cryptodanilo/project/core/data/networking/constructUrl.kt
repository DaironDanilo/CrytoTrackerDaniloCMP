package com.cryptodanilo.project.core.data.networking

fun constructUrl(
    url: String,
): String {
    val baseUrl = "https://rest.coincap.io/v3/"
    return when {
        url.contains(baseUrl) -> url
        url.startsWith("/") -> baseUrl + url.drop(1)
        else -> baseUrl + url
    }
}
