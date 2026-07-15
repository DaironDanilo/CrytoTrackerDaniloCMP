package com.cryptodanilo.project.core.data.networking

import com.cryptodanilo.project.core.domain.util.NetworkError
import com.cryptodanilo.project.core.domain.util.Result
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException

suspend inline fun <reified T> safeCall(apiCall: () -> HttpResponse): Result<T, NetworkError> {
    val response =
        try {
            apiCall()
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(NetworkError.UNKNOWN)
        }
    return responseToResult(response)
}
