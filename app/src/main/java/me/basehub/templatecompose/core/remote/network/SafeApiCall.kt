package me.basehub.templatecompose.core.remote.network

import kotlin.coroutines.cancellation.CancellationException
import me.basehub.templatecompose.core.remote.dto.ApiResponseDto
import me.basehub.templatecompose.core.remote.errormapper.ErrorMapper
import me.basehub.templatecompose.core.remote.errormapper.NetworkException
import retrofit2.Response

/**
 * Handles API envelopes with data and endpoints that intentionally return no data.
 * Pass Unit as noContentValue for an endpoint whose successful response has null data or no body.
 */
suspend fun <T : Any> safeApiCall(
    call: suspend () -> Response<ApiResponseDto<T>>,
    noContentValue: T? = null
): NetworkResult<T> = try {
    val response = call()
    when {
        !response.isSuccessful -> NetworkResult.Error(
            ErrorMapper.fromHttpResponse(response)
        )

        response.code() == 204 || response.code() == 205 -> {
            noContentValue?.let { NetworkResult.Success(it) }
                ?: NetworkResult.Error(
                    NetworkException(
                        NetworkException.Type.INVALID_RESPONSE,
                        "A successful response had no data.",
                        response.code()
                    )
                )
        }

        else -> {
            val body = response.body()
            when {
                body == null -> NetworkResult.Error(
                    NetworkException(
                        NetworkException.Type.INVALID_RESPONSE,
                        "Response body is missing.",
                        response.code()
                    )
                )

                body.success == false -> NetworkResult.Error(
                    ErrorMapper.fromHttpResponse(response, body.message)
                )

                body.success != true -> NetworkResult.Error(
                    NetworkException(
                        NetworkException.Type.INVALID_RESPONSE,
                        "Response success status is missing.",
                        response.code()
                    )
                )

                else -> (body.data ?: noContentValue)?.let { data ->
                    NetworkResult.Success(data, body.message, body.meta)
                } ?: NetworkResult.Error(
                    NetworkException(
                        NetworkException.Type.INVALID_RESPONSE,
                        "Response data is missing.",
                        response.code()
                    )
                )
            }
        }
    }
} catch (error: CancellationException) {
    throw error
} catch (error: Exception) {
    NetworkResult.Error(ErrorMapper.fromException(error))
}
