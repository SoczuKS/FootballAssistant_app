package com.soczuks.footballassistant.update

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.updateDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_updates"
)

@Singleton
class UpdatePreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val lastCheckAtKey = longPreferencesKey("last_check_at")
    private val dismissedVersionCodeKey = intPreferencesKey("dismissed_version_code")
    private val activeDownloadIdKey = longPreferencesKey("active_download_id")
    private val activeDownloadVersionCodeKey = intPreferencesKey("active_download_version_code")

    suspend fun lastCheckAt(): Long =
        context.updateDataStore.data.first()[lastCheckAtKey] ?: 0L

    suspend fun markChecked(timestamp: Long) {
        context.updateDataStore.edit { it[lastCheckAtKey] = timestamp }
    }

    suspend fun dismissedVersionCode(): Int? =
        context.updateDataStore.data.first()[dismissedVersionCodeKey]

    suspend fun dismiss(versionCode: Int) {
        context.updateDataStore.edit { it[dismissedVersionCodeKey] = versionCode }
    }

    suspend fun saveActiveDownload(downloadId: Long, versionCode: Int) {
        context.updateDataStore.edit {
            it[activeDownloadIdKey] = downloadId
            it[activeDownloadVersionCodeKey] = versionCode
        }
    }

    suspend fun activeDownload(): Pair<Long, Int>? {
        val preferences = context.updateDataStore.data.first()
        val id = preferences[activeDownloadIdKey] ?: return null
        val versionCode = preferences[activeDownloadVersionCodeKey] ?: return null
        return id to versionCode
    }

    suspend fun clearActiveDownload() {
        context.updateDataStore.edit {
            it.remove(activeDownloadIdKey)
            it.remove(activeDownloadVersionCodeKey)
        }
    }
}
