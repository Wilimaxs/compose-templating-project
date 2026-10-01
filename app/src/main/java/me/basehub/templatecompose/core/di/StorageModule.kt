package me.basehub.templatecompose.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

// Keep one instance per file; DataStore must not have multiple active instances for one file.
private val Context.appPreferencesDataStore by preferencesDataStore(name = "app_preferences")
private val Context.securePreferencesDataStore by preferencesDataStore(name = "secure_state")

/** Marks the DataStore that holds encrypted, device-local values. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SecurePreferences

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    // Supply the same Preferences DataStore to every injected consumer.
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.appPreferencesDataStore

    // Keep encrypted values separate so backup rules can exclude this file.
    @Provides
    @Singleton
    @SecurePreferences
    fun provideSecurePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.securePreferencesDataStore
}
