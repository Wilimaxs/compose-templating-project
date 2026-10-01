package me.basehub.templatecompose.core.local.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    @PublishedApi internal val dataStore: DataStore<Preferences>,
    @PublishedApi internal val json: Json
) {

    // Save data primitive (include: Boolean, String, Int, Double, Long)
    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        dataStore.edit { pref ->
            pref[key] = value
        }
    }

    // Read data primitive with flow
    fun <T> get(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data.map { pref ->
            pref[key] ?: defaultValue
        }
    }

    // Store an @Serializable object, map, or list as a JSON string.
    suspend inline fun <reified T> saveObject(key: Preferences.Key<String>, value: T) {
        val jsonString = json.encodeToString(value)
        save(key, jsonString)
    }

    // Decode a stored JSON string into an @Serializable type.
    inline fun <reified T> getObject(key: Preferences.Key<String>): Flow<T?> {
        return dataStore.data.map { pref ->
            val jsonString = pref[key]
            if (!jsonString.isNullOrEmpty()) {
                try {
                    json.decodeFromString<T>(jsonString)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to parse JSON from DataStore for key: ${key.name}")
                    null
                }
            } else null
        }
    }

    // Delete a specific key
    suspend fun <T> remove(key: Preferences.Key<T>) {
        dataStore.edit { pref ->
            pref.remove(key)
        }
    }
}
