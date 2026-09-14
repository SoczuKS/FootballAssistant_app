package com.soczuks.footballassistant.ui.auth

sealed interface StartupState {
    data object Loading : StartupState
    data object Authenticated : StartupState
    data object Unauthenticated : StartupState
    data object Unavailable : StartupState
}
