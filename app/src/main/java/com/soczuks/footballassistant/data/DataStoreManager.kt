package com.soczuks.footballassistant.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) :
    SessionManager {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val userIdKey = stringPreferencesKey("user_id")

    override suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { preferences -> preferences[accessTokenKey] }.first()
    }

    override suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { preferences -> preferences[refreshTokenKey] }.first()
    }

    override suspend fun getUserId(): Int? {
        return context.dataStore.data.map { preferences -> preferences[userIdKey]?.toIntOrNull() }
            .first()
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = userId.toString()
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
            preferences.remove(userIdKey)
        }
    }
}
