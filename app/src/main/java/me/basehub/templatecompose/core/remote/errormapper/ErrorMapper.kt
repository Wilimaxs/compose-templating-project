package me.basehub.templatecompose.core.remote.errormapper

import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import retrofit2.HttpException
import retrofit2.Response

object ErrorMapper {

    fun fromHttpResponse(
        response: Response<*>,
        responseMessage: String? = null
    ): NetworkException {
        val statusCode = response.code()
        val type = when (statusCode) {
            in 200..299 -> NetworkException.Type.API_ERROR
            400 -> NetworkException.Type.BAD_REQUEST
            401 -> NetworkException.Type.UNAUTHORIZED
            403 -> NetworkException.Type.FORBIDDEN
            404 -> NetworkException.Type.NOT_FOUND
            405 -> NetworkException.Type.METHOD_NOT_ALLOWED
            406 -> NetworkException.Type.NOT_ACCEPTABLE
            408, 504 -> NetworkException.Type.TIMEOUT
            409 -> NetworkException.Type.CONFLICT
            422 -> NetworkException.Type.UNPROCESSABLE
            429 -> NetworkException.Type.TOO_MANY_REQUESTS
            503 -> NetworkException.Type.SERVICE_UNAVAILABLE
            in 500..599 -> NetworkException.Type.SERVER_ERROR
            else -> NetworkException.Type.HTTP_ERROR
        }

        // An unreadable error body must not hide the server's HTTP status.
        val body = try {
            response.errorBody()?.string()
        } catch (_: IOException) {
            null
        }
        val message = responseMessage?.trim()?.takeIf { it.isNotEmpty() }
            ?: extractMessage(body)
            ?: defaultMessage(type, statusCode)
        return NetworkException(type, message, statusCode)
    }

    /** Classifies failures that happened without a usable HTTP response. */
    fun fromException(error: Exception): NetworkException {
        if (error is CancellationException) throw error
        if (error is HttpException) {
            return error.response()?.let { fromHttpResponse(it) }
                ?: NetworkException(
                    NetworkException.Type.HTTP_ERROR,
                    "Request failed with HTTP ${error.code()}.",
                    error.code()
                )
        }

        val type = when (error) {
            is InterruptedIOException -> NetworkException.Type.TIMEOUT
            is SerializationException -> NetworkException.Type.INVALID_RESPONSE
            is IOException -> NetworkException.Type.CONNECTION
            else -> NetworkException.Type.UNEXPECTED
        }
        return NetworkException(type, defaultMessage(type))
    }

    /** Reads common JSON message fields without requiring a backend-specific DTO. */
    private fun extractMessage(body: String?): String? {
        if (body.isNullOrBlank()) return null
        // TODO(template): Adapt these fields if your backend uses a different error schema.
        val json = try {
            Json.parseToJsonElement(body) as? JsonObject
        } catch (_: SerializationException) {
            null
        } ?: return null

        return listOf("message", "error", "info", "note").firstNotNullOfOrNull { key ->
            (json[key] as? JsonPrimitive)
                ?.contentOrNull
                ?.trim()
                ?.takeIf { it.isNotEmpty() && it != "null" }
        }
    }

    /** Provides a safe fallback until the app adds localized error strings. */
    // TODO(template): Replace these fallback messages with localized app resources when needed.
    private fun defaultMessage(type: NetworkException.Type, statusCode: Int? = null): String =
        when (type) {
            NetworkException.Type.BAD_REQUEST -> "The request could not be processed."
            NetworkException.Type.UNAUTHORIZED -> "Please sign in to continue."
            NetworkException.Type.FORBIDDEN -> "You do not have permission to do this."
            NetworkException.Type.NOT_FOUND -> "The requested item was not found."
            NetworkException.Type.METHOD_NOT_ALLOWED -> "This action is not supported."
            NetworkException.Type.NOT_ACCEPTABLE -> "The server cannot provide the requested response."
            NetworkException.Type.TIMEOUT -> "The request timed out. Please try again."
            NetworkException.Type.CONFLICT -> "This request conflicts with existing data."
            NetworkException.Type.UNPROCESSABLE -> "Please check the information and try again."
            NetworkException.Type.TOO_MANY_REQUESTS -> "Too many requests. Please try again later."
            NetworkException.Type.SERVER_ERROR -> "The server is having a problem. Please try again."
            NetworkException.Type.SERVICE_UNAVAILABLE -> "The service is temporarily unavailable."
            NetworkException.Type.HTTP_ERROR -> "Request failed with HTTP ${statusCode ?: "unknown"}."
            NetworkException.Type.API_ERROR -> "The request could not be completed."
            NetworkException.Type.CONNECTION -> "Could not connect to the server."
            NetworkException.Type.INVALID_RESPONSE -> "Could not read the server response."
            NetworkException.Type.UNEXPECTED -> "An unexpected error occurred."
        }
}
