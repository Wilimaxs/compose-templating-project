package me.basehub.templatecompose.core.remote.network

import me.basehub.templatecompose.core.remote.errormapper.NetworkException
import me.basehub.templatecompose.core.remote.dto.ApiMeta

sealed class NetworkResult<out T> {

    data class Success<out T>(
        val data: T,
        val message: String? = null,
        val meta: ApiMeta? = null
    ) : NetworkResult<T>()

    /** Preserves the mapped error type so callers can handle more than its message. */
    data class Error(val error: NetworkException) : NetworkResult<Nothing>() {
        val message: String get() = error.message
        val code: Int? get() = error.statusCode
    }
}

inline fun <T> NetworkResult<T>.onSuccess(
    action: (NetworkResult.Success<T>) -> Unit
): NetworkResult<T> {
    if (this is NetworkResult.Success) action(this)
    return this
}

inline fun <T> NetworkResult<T>.onError(
    action: (NetworkResult.Error) -> Unit
): NetworkResult<T> {
    if (this is NetworkResult.Error) action(this)
    return this
}
