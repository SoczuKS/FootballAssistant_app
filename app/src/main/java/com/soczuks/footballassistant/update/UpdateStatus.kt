package com.soczuks.footballassistant.update

import com.soczuks.footballassistant.api.model.AppRelease

sealed interface UpdateStatus {
    data object Idle : UpdateStatus
    data object Checking : UpdateStatus
    data object UpToDate : UpdateStatus
    data class Available(val release: AppRelease, val required: Boolean) : UpdateStatus
    data class Downloading(val release: AppRelease, val required: Boolean, val progress: Int?) :
        UpdateStatus

    data class Verifying(val release: AppRelease, val required: Boolean) : UpdateStatus
    data class ReadyToInstall(val release: AppRelease, val required: Boolean) : UpdateStatus
    data class Error(
        val kind: UpdateError, val release: AppRelease? = null, val required: Boolean = false
    ) : UpdateStatus
}
