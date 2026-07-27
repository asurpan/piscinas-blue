package com.sagon.piscinasblue.data

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
        val POOL_ID = stringPreferencesKey("pool_id")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val IS_GENUINE = booleanPreferencesKey("is_genuine")
        val HAS_SET_VOLUME = booleanPreferencesKey("has_set_volume")
    }

    val poolId: Flow<String?> = context.dataStore.data
        .map { it[POOL_ID] }

    val location: Flow<Pair<Double, Double>?> = context.dataStore.data
        .map { prefs ->
            val lat = prefs[LATITUDE]
            val lon = prefs[LONGITUDE]
            if (lat != null && lon != null) Pair(lat, lon) else null
        }

    suspend fun setLocation(lat: Double, lon: Double) {
        context.dataStore.edit {
            it[LATITUDE] = lat
            it[LONGITUDE] = lon
        }
    }

    val isGenuine: Flow<Boolean> = context.dataStore.data
        .map { it[IS_GENUINE] ?: false }

    suspend fun setGenuine(genuine: Boolean) {
        context.dataStore.edit { it[IS_GENUINE] = genuine }
    }

    suspend fun setPoolId(id: String) {
        context.dataStore.edit { it[POOL_ID] = id }
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

    val hasSetVolume: Flow<Boolean> = context.dataStore.data
        .map { it[HAS_SET_VOLUME] ?: false }

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

    suspend fun setHasSetVolume(set: Boolean) {
        context.dataStore.edit { it[HAS_SET_VOLUME] = set }
    }
}
