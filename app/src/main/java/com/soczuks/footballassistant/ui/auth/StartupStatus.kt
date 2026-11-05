package com.soczuks.footballassistant.ui.auth

sealed interface StartupStatus {
    data object Loading : StartupStatus
    data object Authenticated : StartupStatus
    data object Unauthenticated : StartupStatus
    data object Unavailable : StartupStatus
}
