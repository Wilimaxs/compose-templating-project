package me.basehub.templatecompose.core.config

import me.basehub.templatecompose.BuildConfig

/**
 * Exposes build environment settings to features without scattering BuildConfig references.
 * The Gradle flavor supplies these values; a feature that does not use an API can ignore them.
 */
object AppConfig {
    val environmentName: String = BuildConfig.FLAVOR
    val apiBaseUrl: String = BuildConfig.API_BASE_URL
}
