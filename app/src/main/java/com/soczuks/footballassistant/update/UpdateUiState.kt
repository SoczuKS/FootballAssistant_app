package com.soczuks.footballassistant.update

import com.soczuks.footballassistant.api.model.AppRelease

sealed interface UpdateUiState {
    data object Idle : UpdateUiState
    data object Checking : UpdateUiState
    data object UpToDate : UpdateUiState
    data class Available(val release: AppRelease, val required: Boolean) : UpdateUiState
    data class Downloading(val release: AppRelease, val required: Boolean, val progress: Int?) :
        UpdateUiState

    data class Verifying(val release: AppRelease, val required: Boolean) : UpdateUiState
    data class ReadyToInstall(val release: AppRelease, val required: Boolean) : UpdateUiState
    data class Error(
        val kind: UpdateError,
        val release: AppRelease? = null,
        val required: Boolean = false
    ) : UpdateUiState
}
