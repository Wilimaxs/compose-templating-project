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
import javax.inject.Singleton

// Keep one DataStore instance for this file throughout the application.
private val Context.appPreferencesDataStore by preferencesDataStore(name = "app_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // Supply the same Preferences DataStore to every injected consumer.
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.appPreferencesDataStore
}
