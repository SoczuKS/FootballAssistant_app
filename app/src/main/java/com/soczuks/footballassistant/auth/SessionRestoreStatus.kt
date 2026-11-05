package com.soczuks.footballassistant.auth

sealed interface SessionRestoreStatus {
    data object Authenticated : SessionRestoreStatus
    data object Unauthenticated : SessionRestoreStatus
    data object Unavailable : SessionRestoreStatus
}
