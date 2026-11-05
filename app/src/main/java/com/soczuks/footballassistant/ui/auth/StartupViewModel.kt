package com.soczuks.footballassistant.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.auth.SessionRestoreStatus
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
    private val _startupStatus = MutableStateFlow<StartupStatus>(StartupStatus.Loading)
    val startupStatus = _startupStatus.asStateFlow()

    init {
        restoreSession()
    }

    fun restoreSession() {
        viewModelScope.launch {
            _startupStatus.value = when (sessionRestorer.restore()) {
                SessionRestoreStatus.Authenticated -> StartupStatus.Authenticated
                SessionRestoreStatus.Unauthenticated -> StartupStatus.Unauthenticated
                SessionRestoreStatus.Unavailable -> StartupStatus.Unavailable
            }
        }
    }
}
