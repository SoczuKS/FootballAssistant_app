package com.soczuks.footballassistant.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.auth.SessionRestoreResult
import com.soczuks.footballassistant.auth.SessionRestorer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val sessionRestorer: SessionRestorer
) : ViewModel() {
    private val _uiState = MutableStateFlow<StartupState>(StartupState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        restoreSession()
    }

    fun restoreSession() {
        viewModelScope.launch {
            _uiState.value = when (sessionRestorer.restore()) {
                SessionRestoreResult.Authenticated -> StartupState.Authenticated
                SessionRestoreResult.Unauthenticated -> StartupState.Unauthenticated
                SessionRestoreResult.Unavailable -> StartupState.Unavailable
            }
        }
    }
}

sealed interface StartupState {
    data object Loading : StartupState
    data object Authenticated : StartupState
    data object Unauthenticated : StartupState
    data object Unavailable : StartupState
}