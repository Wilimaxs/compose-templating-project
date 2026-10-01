package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents the user payload used by the Flutter starter as an initial API example. */
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
    val gender: String? = null,
    @SerialName("user_status")
    val status: String? = null,
    @SerialName("user_picture")
    val picture: String? = null,
    @SerialName("bio_token")
    val bioToken: String? = null
)
