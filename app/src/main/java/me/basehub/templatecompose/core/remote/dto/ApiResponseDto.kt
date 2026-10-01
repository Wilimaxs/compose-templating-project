package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Wraps the status, message, data, and optional pagination metadata returned by the POS API. */
// TODO(template): Match this envelope to the response format of your own backend.
@Serializable
data class ApiResponseDto<T>(
    @SerialName("status")
    val status: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("data")
    val data: T? = null,
    @SerialName("meta")
    val meta: ApiMeta? = null
)

/** Describes the pagination fields returned alongside a POS product list. */
// TODO(template): Rename these fields if your backend uses different pagination metadata.
@Serializable
data class ApiMeta(
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("total_page")
    val totalPage: Int,
    @SerialName("total_data")
    val totalData: Int
)
