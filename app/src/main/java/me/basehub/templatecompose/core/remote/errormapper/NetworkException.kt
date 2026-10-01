package me.basehub.templatecompose.core.remote.errormapper

data class NetworkException(
    val type: Type,
    val message: String,
    val statusCode: Int? = null
) {
    enum class Type {
        BAD_REQUEST,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        METHOD_NOT_ALLOWED,
        NOT_ACCEPTABLE,
        TIMEOUT,
        CONFLICT,
        UNPROCESSABLE,
        TOO_MANY_REQUESTS,
        SERVER_ERROR,
        SERVICE_UNAVAILABLE,
        HTTP_ERROR,
        API_ERROR,
        CONNECTION,
        INVALID_RESPONSE,
        UNEXPECTED
    }
}
