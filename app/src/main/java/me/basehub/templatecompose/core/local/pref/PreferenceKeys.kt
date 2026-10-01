package me.basehub.templatecompose.core.local.pref

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Sample keys for DataStoreManager.save/get and saveObject/getObject.
 * TODO(template): Rename or remove these keys when adapting the starter to your app.
 */
object PreferenceKeys {
    // Boolean example: DataStoreManager.save/get.
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")

    // String example: DataStoreManager.save/get.
    val APP_LANGUAGE = stringPreferencesKey("app_language")

    // Int example: DataStoreManager.save/get.
    val ITEMS_PER_PAGE = intPreferencesKey("items_per_page")

    // Long example: DataStoreManager.save/get.
    val LAST_SYNC_AT = longPreferencesKey("last_sync_at")

    // Double example: DataStoreManager.save/get.
    val TEXT_SCALE = doublePreferencesKey("text_scale")

    // JSON example: DataStoreManager.saveObject/getObject with an @Serializable type.
    val HOME_FILTER_JSON = stringPreferencesKey("home_filter_json")
}
