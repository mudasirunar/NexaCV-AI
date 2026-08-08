package com.mudasir.nexacvai.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

import androidx.datastore.preferences.core.stringPreferencesKey
import com.mudasir.nexacvai.domain.model.ProfileSortOrder

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nexacvai_settings")

@Singleton
open class AppSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val PROFILE_SORT_ORDER = stringPreferencesKey("profile_sort_order")
        val THEME_MODE = stringPreferencesKey("app_theme_mode")
    }

    open val themeModeFlow: Flow<com.mudasir.nexacvai.domain.model.AppThemeMode>
        get() = context.dataStore.data.map { preferences ->
            com.mudasir.nexacvai.domain.model.AppThemeMode.fromId(preferences[THEME_MODE])
        }

    open val hasSeenOnboardingFlow: Flow<Boolean>
        get() = context.dataStore.data.map { preferences ->
            preferences[HAS_SEEN_ONBOARDING] ?: false
        }

    open val profileSortOrderFlow: Flow<ProfileSortOrder>
        get() = context.dataStore.data.map { preferences ->
            ProfileSortOrder.fromId(preferences[PROFILE_SORT_ORDER])
        }

    open suspend fun setThemeMode(themeMode: com.mudasir.nexacvai.domain.model.AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.id
        }
    }


    open suspend fun setHasSeenOnboarding(hasSeen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_ONBOARDING] = hasSeen
        }
    }

    open suspend fun setProfileSortOrder(sortOrder: ProfileSortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_SORT_ORDER] = sortOrder.id
        }
    }
}
