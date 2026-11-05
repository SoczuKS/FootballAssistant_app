package com.soczuks.footballassistant.auth

sealed interface SessionValidationStatus {
    data class Valid(val userId: Int) : SessionValidationStatus
    data object Invalid : SessionValidationStatus
    data object Unavailable : SessionValidationStatus
}
