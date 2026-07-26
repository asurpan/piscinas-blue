package com.sagon.myapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USAGE_COUNT = intPreferencesKey("usage_count")
        val IS_ACTIVATED = booleanPreferencesKey("is_activated")
        val TABLET_ACTION_COUNT = intPreferencesKey("tablet_action_count")
        val HAS_SEEN_TABLET_INFO = booleanPreferencesKey("has_seen_tablet_info")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { it[ONBOARDING_COMPLETED] ?: false }

    val usageCount: Flow<Int> = context.dataStore.data
        .map { it[USAGE_COUNT] ?: 0 }

    val isActivated: Flow<Boolean> = context.dataStore.data
        .map { it[IS_ACTIVATED] ?: false }

    val tabletActionCount: Flow<Int> = context.dataStore.data
        .map { it[TABLET_ACTION_COUNT] ?: 0 }

    val hasSeenTabletInfo: Flow<Boolean> = context.dataStore.data
        .map { it[HAS_SEEN_TABLET_INFO] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun incrementUsage() {
        context.dataStore.edit { 
            val current = it[USAGE_COUNT] ?: 0
            it[USAGE_COUNT] = current + 1 
        }
    }

    suspend fun incrementTabletAction() {
        context.dataStore.edit { 
            val current = it[TABLET_ACTION_COUNT] ?: 0
            it[TABLET_ACTION_COUNT] = current + 1 
        }
    }

    suspend fun setHasSeenTabletInfo(seen: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_TABLET_INFO] = seen }
    }

    suspend fun setActivated(activated: Boolean) {
        context.dataStore.edit { it[IS_ACTIVATED] = activated }
    }
}
