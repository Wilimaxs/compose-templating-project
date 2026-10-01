package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
