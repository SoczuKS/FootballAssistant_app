package com.soczuks.footballassistant.auth

sealed interface SessionValidationResult {
    data class Valid(val userId: Int): SessionValidationResult
    data object Invalid : SessionValidationResult
    data object Unavailable : SessionValidationResult
}