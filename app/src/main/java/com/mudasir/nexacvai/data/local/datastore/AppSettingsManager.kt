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
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val PROFILE_SORT_ORDER = stringPreferencesKey("profile_sort_order")
    }

    open val isDarkModeFlow: Flow<Boolean?>
        get() = context.dataStore.data.map { preferences ->
            preferences[IS_DARK_MODE]
        }

    open val hasSeenOnboardingFlow: Flow<Boolean>
        get() = context.dataStore.data.map { preferences ->
            preferences[HAS_SEEN_ONBOARDING] ?: false
        }

    open val profileSortOrderFlow: Flow<ProfileSortOrder>
        get() = context.dataStore.data.map { preferences ->
            ProfileSortOrder.fromId(preferences[PROFILE_SORT_ORDER])
        }

    open suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
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
