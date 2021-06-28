package edu.nuce.apps.newflowtypes.shared.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    suspend fun setAccessToken(accessToken: String)
    suspend fun getAccessToken(): String
    val accessToken: Flow<String>
    suspend fun clearAccessToken()

    suspend fun setRefreshToken(refreshToken: String)
    suspend fun getRefreshToken(): String
    suspend fun clearRefreshToken()
}

@Singleton
class DataStorePreferenceStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceStorage {

    companion object {
        const val PREFS_NAME = "meeting"
    }

    object PreferencesKeys {
        val PREF_ACCESS_TOKEN = stringPreferencesKey("pref_access_token")
        val PREF_REFRESH_TOKEN = stringPreferencesKey("pref_refresh_token")
    }

    override suspend fun setAccessToken(accessToken: String) {
        dataStore.edit {
            it[PreferencesKeys.PREF_ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun getAccessToken(): String {
        return dataStore.data.map { it[PreferencesKeys.PREF_ACCESS_TOKEN] ?: "" }.first()
    }

    override val accessToken: Flow<String> = dataStore.data.map {
        it[PreferencesKeys.PREF_ACCESS_TOKEN] ?: ""
    }

    override suspend fun clearAccessToken() {
        dataStore.edit {
            it.remove(PreferencesKeys.PREF_ACCESS_TOKEN)
        }
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit {
            it[PreferencesKeys.PREF_REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun getRefreshToken(): String {
        return dataStore.data.map { it[PreferencesKeys.PREF_REFRESH_TOKEN] ?: "" }.first()
    }

    override suspend fun clearRefreshToken() {
        dataStore.edit {
            it.remove(PreferencesKeys.PREF_REFRESH_TOKEN)
        }
    }
}