package me.basehub.templatecompose.core.auth

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.basehub.templatecompose.core.local.secure.SecureStorageManager
import me.basehub.templatecompose.core.local.storage.StorageManager
import me.basehub.templatecompose.core.remote.dto.EmployeeDto
import timber.log.Timber

/** Owns the app-wide session and restores it when the application starts. */
@Singleton
class AuthManager @Inject constructor(
    private val secureStorage: SecureStorageManager,
    private val storage: StorageManager
) {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val started = AtomicBoolean(false)
    private val sessionMutex = Mutex()
    private val mutableState = MutableStateFlow<AuthState>(AuthState.Loading)

    val state: StateFlow<AuthState> = mutableState.asStateFlow()

    /** Trigger the initial session check once after application setup. */
    fun start() {
        if (started.compareAndSet(false, true)) {
            appScope.launch { restoreSession() }
        }
    }

    /** Recheck local session data, for example after a startup error. */
    suspend fun restoreSession() = sessionMutex.withLock {
        mutableState.value = AuthState.Loading
        try {
            val token = secureStorage.getToken()
            val bypassEnabled = storage.get(BYPASS_ENABLED, false).first()
            val cachedEmployee = storage.getObject<EmployeeDto>(CACHED_EMPLOYEE).first()

            // TODO(template): Ask your backend to validate or refresh an API token when available.
            mutableState.value = when {
                !token.isNullOrBlank() -> AuthState.Authenticated(cachedEmployee, AuthMode.API)
                bypassEnabled -> AuthState.Authenticated(
                    cachedEmployee ?: demoEmployee(),
                    AuthMode.BYPASS
                )
                else -> AuthState.Unauthenticated
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Timber.e(error, "Could not restore the saved session.")
            mutableState.value = AuthState.Error("Could not check the saved session.")
        }
    }

    /** Persist an API session after login; the token is stored separately from the employee. */
    suspend fun saveApiSession(token: String, employee: EmployeeDto?) = sessionMutex.withLock {
        require(token.isNotBlank()) { "The login token must not be blank." }

        storage.save(BYPASS_ENABLED, false)
        if (employee == null) {
            storage.remove(CACHED_EMPLOYEE)
        } else {
            storage.saveObject(CACHED_EMPLOYEE, employee)
        }
        secureStorage.setToken(token)
        mutableState.value = AuthState.Authenticated(employee, AuthMode.API)
    }

    /** Enter the starter's hardcoded session without creating a backend token. */
    suspend fun useBypassSession() = sessionMutex.withLock {
        val employee = demoEmployee()
        secureStorage.clearToken()
        storage.saveObject(CACHED_EMPLOYEE, employee)
        storage.save(BYPASS_ENABLED, true)
        mutableState.value = AuthState.Authenticated(employee, AuthMode.BYPASS)
    }

    /** End either kind of session without clearing unrelated app preferences. */
    suspend fun signOut() = sessionMutex.withLock {
        clearSessionLocked()
    }

    /** Sign out only when a 401 rejected the access token that is still active. */
    suspend fun signOutIfCurrentToken(rejectedToken: String): Boolean = sessionMutex.withLock {
        if (secureStorage.getToken() != rejectedToken) return@withLock false
        clearSessionLocked()
        true
    }

    private suspend fun clearSessionLocked() {
        storage.save(BYPASS_ENABLED, false)
        secureStorage.clearToken()
        mutableState.value = AuthState.Unauthenticated
        storage.remove(CACHED_EMPLOYEE)
    }

    // TODO(template): Replace this demo profile with the sample data your starter should show.
    private fun demoEmployee() = EmployeeDto(
        employeeCode = "DEMO-EMPLOYEE",
        storeCode = "DEMO-STORE",
        storeName = "Demo Store",
        name = "Demo Employee",
        phone = "081200000000",
        position = "Demo",
        isOwner = true
    )

    private companion object {
        private val CACHED_EMPLOYEE = stringPreferencesKey("auth_cached_employee_json")
        private val BYPASS_ENABLED = booleanPreferencesKey("auth_bypass_enabled")
    }
}
