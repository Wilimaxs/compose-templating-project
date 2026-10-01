package me.basehub.templatecompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import me.basehub.templatecompose.core.auth.AuthManager
import timber.log.Timber

/**
 * Creates the application-level Hilt component and installs debug logging at once.
 */
@HiltAndroidApp
class TemplateApplication : Application() {
    @Inject lateinit var authManager: AuthManager

    override fun onCreate() {
        super.onCreate()

        // Keep template logs visible during development without logging in release builds.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Restore the saved session once at process startup; the root UI will observe its state.
        authManager.start()
    }
}
