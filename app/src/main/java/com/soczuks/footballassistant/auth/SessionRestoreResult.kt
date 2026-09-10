package com.soczuks.footballassistant.auth

sealed interface SessionRestoreResult {
    data object Authenticated : SessionRestoreResult
    data object Unauthenticated : SessionRestoreResult
    data object Unavailable : SessionRestoreResult
}
