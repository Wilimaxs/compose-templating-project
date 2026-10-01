package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Represents the employee profile returned by the POS login endpoint. */
// TODO(template): Replace these fields when integrating a backend with a different account model.
@Serializable
data class EmployeeDto(
    @SerialName("employee_code")
    val employeeCode: String,
    @SerialName("store_code")
    val storeCode: String,
    @SerialName("store_name")
    val storeName: String,
    @SerialName("name")
    val name: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("position")
    val position: String,
    @SerialName("is_owner")
    val isOwner: Boolean
)
