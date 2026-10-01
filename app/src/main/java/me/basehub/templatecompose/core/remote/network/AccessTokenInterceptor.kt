package me.basehub.templatecompose.core.remote.network

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import me.basehub.templatecompose.BuildConfig
import me.basehub.templatecompose.core.auth.AuthManager
import me.basehub.templatecompose.core.local.secure.SecureStorageManager
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import timber.log.Timber

/** Mark Retrofit methods that must not receive an access token, such as login. */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NoAuth

/** Adds the stored access token and ends the matching session when the server returns 401. */
@Singleton
class AccessTokenInterceptor @Inject constructor(
    private val secureStorage: SecureStorageManager,
    private val authManager: AuthManager
) : Interceptor {
    private val apiOrigin = BuildConfig.BASE_URL.toHttpUrl()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val sameApiOrigin = request.url.scheme == apiOrigin.scheme &&
                request.url.host == apiOrigin.host &&
                request.url.port == apiOrigin.port
        val publicEndpoint = request.tag(Invocation::class.java)
            ?.method()
            ?.isAnnotationPresent(NoAuth::class.java) == true

        // Keep credentials off other hosts, public endpoints, and manually authorized requests.
        if (!sameApiOrigin || publicEndpoint || request.header(AUTHORIZATION) != null) {
            return chain.proceed(request)
        }

        // OkHttp interceptors are synchronous; DataStore exposes a suspend read.
        val token = runBlocking { secureStorage.getToken() }?.takeIf { it.isNotBlank() }
        val authorizedRequest = if (token == null) {
            request
        } else {
            request.newBuilder()
                .header(AUTHORIZATION, "Bearer $token")
                .build()
        }

        val response = chain.proceed(authorizedRequest)
        if (response.code == HTTP_UNAUTHORIZED && token != null) {
            try {
                // A late 401 must not sign out a newer session with a different token.
                runBlocking { authManager.signOutIfCurrentToken(token) }
            } catch (error: Exception) {
                Timber.e(error, "Could not clear the rejected access token.")
            }
        }
        return response
    }

    private companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val HTTP_UNAUTHORIZED = 401
    }
}
