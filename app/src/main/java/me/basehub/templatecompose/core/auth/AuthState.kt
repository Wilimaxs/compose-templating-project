package me.basehub.templatecompose.core.auth

import me.basehub.templatecompose.core.remote.dto.EmployeeDto

/** Identifies how the current session was created. */
enum class AuthMode {
    API,
    BYPASS
}

/** Represents the app-wide session status observed by the root UI. */
sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val employee: EmployeeDto?, val mode: AuthMode) : AuthState
    data class Error(val message: String) : AuthState
}
