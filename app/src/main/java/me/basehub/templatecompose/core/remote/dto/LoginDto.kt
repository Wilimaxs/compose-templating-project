package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Sends the phone number and password required by the POS login endpoint. */
// TODO(template): Change the login identifier and payload fields for your own backend.
@Serializable
data class LoginRequestDto(
    @SerialName("phone")
    val phone: String,
    @SerialName("password")
    val password: String
)

/** Keeps the login token separate from the employee profile stored in the app session. */
@Serializable
data class LoginDataDto(
    @SerialName("token")
    val token: String,
    @SerialName("employee")
    val employee: EmployeeDto,
    // TODO(template): Adapt this type if your backend returns structured permission objects.
    @SerialName("permissions")
    val permissions: List<String> = emptyList()
)
