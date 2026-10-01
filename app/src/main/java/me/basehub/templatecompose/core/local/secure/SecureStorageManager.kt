package me.basehub.templatecompose.core.local.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.basehub.templatecompose.core.di.SecurePreferences
import timber.log.Timber

/** Encrypts session tokens with an Android Keystore key before storing them in DataStore. */
// TODO(template): Add encrypted biometric settings here when the biometric session model is defined.
@Singleton
class SecureStorageManager @Inject constructor(
    @SecurePreferences private val secureDataStore: DataStore<Preferences>
) {
    private val keyLock = Any()

    /** Encrypt and save a token; null or blank removes the current token. */
    suspend fun setToken(value: String?) {
        if (value.isNullOrBlank()) {
            clearToken()
            return
        }

        val encryptedToken = withContext(Dispatchers.IO) { encrypt(value) }
        secureDataStore.edit { preferences ->
            preferences[ENCRYPTED_TOKEN] = encryptedToken
        }
    }

    /** Decrypt the stored token, or return null when no usable token exists. */
    suspend fun getToken(): String? {
        val encryptedToken = secureDataStore.data.first()[ENCRYPTED_TOKEN] ?: return null

        return try {
            withContext(Dispatchers.IO) { decrypt(encryptedToken) }
        } catch (error: GeneralSecurityException) {
            clearUnreadableToken(encryptedToken, error)
            null
        } catch (error: IllegalArgumentException) {
            clearUnreadableToken(encryptedToken, error)
            null
        }
    }

    /** Remove the encrypted token while keeping the Keystore key for later use. */
    suspend fun clearToken() {
        secureDataStore.edit { preferences ->
            preferences.remove(ENCRYPTED_TOKEN)
        }
    }

    private suspend fun clearUnreadableToken(encryptedToken: String, error: Exception) {
        Timber.w(error, "Stored token could not be decrypted; clearing it.")
        secureDataStore.edit { preferences ->
            if (preferences[ENCRYPTED_TOKEN] == encryptedToken) {
                preferences.remove(ENCRYPTED_TOKEN)
            }
        }
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val ciphertext = Base64.encodeToString(
            cipher.doFinal(value.toByteArray(Charsets.UTF_8)),
            Base64.NO_WRAP
        )
        return "$iv:$ciphertext"
    }

    private fun decrypt(value: String): String {
        val parts = value.split(':', limit = 2)
        require(parts.size == 2) { "Invalid encrypted token format." }

        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)
        require(iv.size == GCM_IV_BYTES && ciphertext.size >= GCM_TAG_BYTES) {
            "Invalid encrypted token content."
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        return cipher.doFinal(ciphertext).toString(Charsets.UTF_8)
    }

    private fun getOrCreateKey(): SecretKey = synchronized(keyLock) {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey) ?: KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        ).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
            )
        }.generateKey()
    }

    private companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_ALIAS = "template_compose_secure_storage_key"
        private const val GCM_IV_BYTES = 12
        private const val GCM_TAG_BYTES = 16
        private const val GCM_TAG_BITS = 128
        private val ENCRYPTED_TOKEN = stringPreferencesKey("encrypted_auth_token")
    }
}
