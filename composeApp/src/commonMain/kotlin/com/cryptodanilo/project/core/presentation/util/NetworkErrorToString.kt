package com.cryptodanilo.project.core.presentation.util

import com.cryptodanilo.project.core.domain.util.NetworkError
import cryptotrackerdanilo.composeapp.generated.resources.Res
import cryptotrackerdanilo.composeapp.generated.resources.no_internet
import cryptotrackerdanilo.composeapp.generated.resources.request_timeout
import cryptotrackerdanilo.composeapp.generated.resources.serialization_error
import cryptotrackerdanilo.composeapp.generated.resources.server_error
import cryptotrackerdanilo.composeapp.generated.resources.too_many_requests
import cryptotrackerdanilo.composeapp.generated.resources.unknown_error

fun NetworkError.toUiString(): UiText {
    val redId = when (this) {
        NetworkError.REQUEST_TIMEOUT -> Res.string.request_timeout
        NetworkError.TOO_MANY_REQUESTS -> Res.string.too_many_requests
        NetworkError.NO_INTERNET -> Res.string.no_internet
        NetworkError.SERVER_ERROR -> Res.string.server_error
        NetworkError.SERIALIZATION -> Res.string.serialization_error
        NetworkError.UNKNOWN -> Res.string.unknown_error
    }
    return UiText.StringResourceId(redId)
}
