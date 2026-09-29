package me.basehub.templatecompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Creates the application-level Hilt component and installs debug logging at once.
 */
@HiltAndroidApp
class TemplateApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Keep template logs visible during development without logging in release builds.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
