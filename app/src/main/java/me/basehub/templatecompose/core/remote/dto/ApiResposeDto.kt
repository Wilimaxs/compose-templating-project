package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO(template): Match this response envelope to the format used by your backend.
@Serializable
data class ApiResponseDto<T>(
    @SerialName("success")
    val success: Boolean? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("data")
    val data: T? = null,
    @SerialName("meta")
    val meta: ApiMeta? = null,
)

/** Carries pagination metadata for APIs that return it in a meta object. */
// TODO(template): Replace these fields if your backend uses a different pagination format.
@Serializable
data class ApiMeta(
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("last_page")
    val lastPage: Int,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("total")
    val total: Int
)
