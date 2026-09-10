package com.soczuks.footballassistant.auth

import com.soczuks.footballassistant.data.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRestorer @Inject constructor(
    private val sessionManager: SessionManager,
    private val sessionValidator: SessionValidator
) {
    suspend fun restore(): SessionRestoreResult {
        val accessToken = try {
            sessionManager.getAccessToken()
        } catch (_: Exception) {
            return SessionRestoreResult.Unavailable
        }

        if (accessToken.isNullOrBlank()) {
            sessionManager.clearSession()
            return SessionRestoreResult.Unauthenticated
        }

        return when(val validationResult = sessionValidator.validate()) {
            is SessionValidationResult.Valid -> {
                sessionManager.saveUserId(validationResult.userId)
                SessionRestoreResult.Authenticated
            }

            SessionValidationResult.Invalid -> {
                sessionManager.clearSession()
                SessionRestoreResult.Unauthenticated
            }

            SessionValidationResult.Unavailable -> SessionRestoreResult.Unavailable
        }
    }
}