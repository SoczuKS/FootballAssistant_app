package com.soczuks.footballassistant.update

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.soczuks.footballassistant.BuildConfig
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.AppRelease
import com.soczuks.footballassistant.utility.sha256Hex
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class UpdateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi,
    private val preferences: UpdatePreferences
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    private val checking = AtomicBoolean(false)
    private val monitoring = AtomicBoolean(false)

    private val _state = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val state: StateFlow<UpdateUiState> = _state.asStateFlow()

    init {
        scope.launch { removeInstalledUpdateFiles() }
    }

    fun checkForUpdate(force: Boolean = false) {
        if (
            _state.value is UpdateUiState.Downloading ||
            _state.value is UpdateUiState.Verifying
        ) return
        if (!checking.compareAndSet(false, true)) return
        scope.launch {
            try {
                if (!force) {
                    val elapsed = System.currentTimeMillis() - preferences.lastCheckAt()
                    val hasPendingWork = preferences.activeDownload() != null
                    if (!hasPendingWork && elapsed in 0 until CHECK_INTERVAL_MS) return@launch
                }

                if (_state.value == UpdateUiState.Idle || force) {
                    _state.value = UpdateUiState.Checking
                }
                val response = api.getLatestAppRelease()
                preferences.markChecked(System.currentTimeMillis())
                if (!response.isSuccessful) {
                    if (force) _state.value = UpdateUiState.Error(UpdateError.NETWORK)
                    else _state.value = UpdateUiState.Idle
                    return@launch
                }

                val release = response.body()
                if (release == null || !isValidRelease(release)) {
                    if (force) _state.value = UpdateUiState.Error(UpdateError.NETWORK)
                    else _state.value = UpdateUiState.Idle
                    return@launch
                }

                val decision = evaluateUpdate(
                    release = release,
                    dismissedVersionCode = if (force) null else preferences.dismissedVersionCode()
                )
                if (!decision.available) {
                    if (release.versionCode <= BuildConfig.VERSION_CODE) {
                        preferences.clearActiveDownload()
                    }
                    _state.value = if (force) UpdateUiState.UpToDate else UpdateUiState.Idle
                    return@launch
                }

                val activeDownload = preferences.activeDownload()
                if (activeDownload?.second == release.versionCode) {
                    monitorDownload(activeDownload.first, release, decision.required)
                } else {
                    if (activeDownload != null) preferences.clearActiveDownload()
                    val file = updateFile(release.versionCode)
                    if (file.isFile && verifyFile(file, release)) {
                        _state.value = UpdateUiState.ReadyToInstall(release, decision.required)
                    } else {
                        if (file.exists()) file.delete()
                        _state.value = UpdateUiState.Available(release, decision.required)
                    }
                }
            } catch (_: Exception) {
                if (force) _state.value = UpdateUiState.Error(UpdateError.NETWORK)
                else _state.value = UpdateUiState.Idle
            } finally {
                checking.set(false)
            }
        }
    }

    fun dismiss() {
        val current = _state.value
        val release = releaseFrom(current) ?: run {
            _state.value = UpdateUiState.Idle
            return
        }
        if (requiredFrom(current)) return
        scope.launch {
            preferences.dismiss(release.versionCode)
            preferences.clearActiveDownload()
            _state.value = UpdateUiState.Idle
        }
    }

    fun download() {
        val current = _state.value
        val release = releaseFrom(current) ?: return
        val required = requiredFrom(current)
        if (current is UpdateUiState.Downloading || current is UpdateUiState.Verifying) return

        scope.launch {
            try {
                val directory = updatesDirectory()
                directory.mkdirs()
                val file = updateFile(release.versionCode)
                if (file.exists()) file.delete()

                val request = DownloadManager.Request(release.downloadUrl.toUri())
                    .setTitle("Gardener ${release.versionName}")
                    .setDescription(context.getString(R.string.update_downloading))
                    .setMimeType(APK_MIME_TYPE)
                    .setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                    )
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalFilesDir(
                        context,
                        Environment.DIRECTORY_DOWNLOADS,
                        "updates/${file.name}"
                    )

                val downloadId = downloadManager.enqueue(request)
                preferences.saveActiveDownload(downloadId, release.versionCode)
                monitorDownload(downloadId, release, required)
            } catch (_: Exception) {
                _state.value = UpdateUiState.Error(UpdateError.DOWNLOAD, release, required)
            }
        }
    }

    fun hasInstallPermission(): Boolean = context.packageManager.canRequestPackageInstalls()

    fun unknownSourcesIntent(): Intent =
        Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            "package:${context.packageName}".toUri()
        )

    fun install(activity: Activity) {
        val current = _state.value
        val release = releaseFrom(current) ?: return
        val required = requiredFrom(current)
        val file = updateFile(release.versionCode)
        if (!file.isFile || !hasInstallPermission()) {
            _state.value = UpdateUiState.Error(UpdateError.INSTALLATION, release, required)
            return
        }

        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, APK_MIME_TYPE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
            _state.value = UpdateUiState.ReadyToInstall(release, required)
        } catch (_: Exception) {
            _state.value = UpdateUiState.Error(UpdateError.INSTALLATION, release, required)
        }
    }

    private suspend fun monitorDownload(
        downloadId: Long,
        release: AppRelease,
        required: Boolean
    ) {
        if (!monitoring.compareAndSet(false, true)) return
        try {
            while (true) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                cursor.use {
                    if (!it.moveToFirst()) {
                        preferences.clearActiveDownload()
                        if (verifyFile(updateFile(release.versionCode), release)) {
                            _state.value = UpdateUiState.ReadyToInstall(release, required)
                        } else {
                            _state.value = UpdateUiState.Error(
                                UpdateError.DOWNLOAD,
                                release,
                                required
                            )
                        }
                        return
                    }

                    val status = it.getInt(
                        it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                    )
                    val downloaded = it.getLong(
                        it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    )
                    val total = it.getLong(
                        it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    )
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            verifyDownloadedRelease(release, required)
                            return
                        }

                        DownloadManager.STATUS_FAILED -> {
                            preferences.clearActiveDownload()
                            _state.value = UpdateUiState.Error(
                                UpdateError.DOWNLOAD,
                                release,
                                required
                            )
                            return
                        }

                        else -> {
                            val progress = if (total > 0) {
                                ((downloaded * 100L) / total).toInt().coerceIn(0, 100)
                            } else null
                            _state.value = UpdateUiState.Downloading(
                                release,
                                required,
                                progress
                            )
                        }
                    }
                }
                delay(750.milliseconds)
            }
        } finally {
            monitoring.set(false)
        }
    }

    private suspend fun verifyDownloadedRelease(release: AppRelease, required: Boolean) {
        _state.value = UpdateUiState.Verifying(release, required)
        val file = updateFile(release.versionCode)
        if (verifyFile(file, release)) {
            _state.value = UpdateUiState.ReadyToInstall(release, required)
        } else {
            file.delete()
            _state.value = UpdateUiState.Error(UpdateError.VERIFICATION, release, required)
        }
    }

    private suspend fun verifyFile(file: File, release: AppRelease): Boolean =
        withContext(Dispatchers.IO) {
            if (!file.isFile || file.length() != release.sizeBytes) return@withContext false
            val actual = sha256Hex(file)
            actual.equals(release.sha256, ignoreCase = true)
        }

    private fun isValidRelease(release: AppRelease): Boolean =
        release.versionCode > 0 &&
                release.minimumSupportedVersionCode in 1..release.versionCode &&
                release.versionName.isNotBlank() &&
                release.downloadUrl.startsWith("https://") &&
                release.sha256.matches(Regex("^[a-fA-F0-9]{64}$")) &&
                release.sizeBytes > 0

    private fun releaseFrom(state: UpdateUiState): AppRelease? = when (state) {
        is UpdateUiState.Available -> state.release
        is UpdateUiState.Downloading -> state.release
        is UpdateUiState.Verifying -> state.release
        is UpdateUiState.ReadyToInstall -> state.release
        is UpdateUiState.Error -> state.release
        else -> null
    }

    private fun requiredFrom(state: UpdateUiState): Boolean = when (state) {
        is UpdateUiState.Available -> state.required
        is UpdateUiState.Downloading -> state.required
        is UpdateUiState.Verifying -> state.required
        is UpdateUiState.ReadyToInstall -> state.required
        is UpdateUiState.Error -> state.required
        else -> false
    }

    private fun updatesDirectory(): File = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
        "updates"
    )

    private fun updateFile(versionCode: Int): File =
        File(updatesDirectory(), "gardener-$versionCode.apk")

    private fun removeInstalledUpdateFiles() {
        updatesDirectory().listFiles()?.forEach { file ->
            val version = file.name.removePrefix("gardener-").removeSuffix(".apk").toIntOrNull()
            if (version != null && version <= BuildConfig.VERSION_CODE) file.delete()
        }
    }

    private fun evaluateUpdate(
        release: AppRelease,
        dismissedVersionCode: Int?
    ): UpdateDecision {
        val currentVersionCode = BuildConfig.VERSION_CODE
        if (release.versionCode <= currentVersionCode) {
            return UpdateDecision(available = false, required = false)
        }
        val required = currentVersionCode < release.minimumSupportedVersionCode
        val dismissed = !required && dismissedVersionCode == release.versionCode
        return UpdateDecision(available = !dismissed, required = required)
    }

    private companion object {
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
        const val CHECK_INTERVAL_MS = 12L * 60L * 60L * 1000L
    }
}