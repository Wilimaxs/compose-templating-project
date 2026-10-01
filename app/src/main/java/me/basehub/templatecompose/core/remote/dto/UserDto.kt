package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents the user payload returned by the application's backend. */
// TODO(template): Match these fields, types, and JSON names to your backend's user response.
@Serializable
data class UserDto(
    @SerialName("user_id")
    val id: Int? = null,
    @SerialName("user_name")
    val name: String? = null,
    @SerialName("user_email")
    val email: String? = null,
    @SerialName("user_phone")
    val phone: String? = null,
    @SerialName("user_status")
    val status: String? = null,
    @SerialName("user_picture")
    val picture: String? = null
)
