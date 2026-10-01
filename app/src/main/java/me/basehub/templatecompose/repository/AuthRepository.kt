package me.basehub.templatecompose.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import me.basehub.templatecompose.core.auth.AuthManager
import me.basehub.templatecompose.core.remote.api.ApiService
import me.basehub.templatecompose.core.remote.dto.EmployeeDto
import me.basehub.templatecompose.core.remote.errormapper.NetworkException
import me.basehub.templatecompose.core.remote.network.NetworkResult
import me.basehub.templatecompose.core.remote.network.safeApiCall
import timber.log.Timber

/** Connects the login endpoint to the app-wide authenticated session. */
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    /** Signs in with POS credentials and stores the returned token and employee on success. */
    suspend fun login(phone: String, password: String): NetworkResult<EmployeeDto> {
        val result = safeApiCall(call = {
            apiService.login(phone = phone, password = password)
        })

        return when (result) {
            is NetworkResult.Error -> result
            is NetworkResult.Success -> {
                val loginData = result.data
                if (loginData.token.isBlank()) {
                    return NetworkResult.Error(
                        NetworkException(
                            NetworkException.Type.INVALID_RESPONSE,
                            "Login response did not contain an access token."
                        )
                    )
                }

                try {
                    authManager.saveApiSession(loginData.token, loginData.employee)
                    NetworkResult.Success(loginData.employee, result.message)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Timber.e(error, "Could not save the login session.")
                    NetworkResult.Error(
                        NetworkException(
                            NetworkException.Type.UNEXPECTED,
                            "Could not save the login session."
                        )
                    )
                }
            }
        }
    }
}
