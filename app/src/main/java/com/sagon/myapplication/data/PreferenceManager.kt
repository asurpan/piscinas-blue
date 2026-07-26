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
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { it[ONBOARDING_COMPLETED] ?: false }

    val usageCount: Flow<Int> = context.dataStore.data
        .map { it[USAGE_COUNT] ?: 0 }

    val isActivated: Flow<Boolean> = context.dataStore.data
        .map { it[IS_ACTIVATED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun incrementUsage() {
        context.dataStore.edit { 
            val current = it[USAGE_COUNT] ?: 0
            it[USAGE_COUNT] = current + 1 
        }
    }

    suspend fun setActivated(activated: Boolean) {
        context.dataStore.edit { it[IS_ACTIVATED] = activated }
    }
}
