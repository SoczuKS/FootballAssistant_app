package com.soczuks.footballassistant.auth

import com.soczuks.footballassistant.data.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRestorer @Inject constructor(
    private val sessionManager: SessionManager, private val sessionValidator: SessionValidator
) {
    suspend fun restore(): SessionRestoreStatus {
        val accessToken = try {
            sessionManager.getAccessToken()
        } catch (_: Exception) {
            return SessionRestoreStatus.Unavailable
        }

        if (accessToken.isNullOrBlank()) {
            sessionManager.clearSession()
            return SessionRestoreStatus.Unauthenticated
        }

        return when (val validationResult = sessionValidator.validate()) {
            is SessionValidationStatus.Valid -> {
                sessionManager.saveUserId(validationResult.userId)
                SessionRestoreStatus.Authenticated
            }

            SessionValidationStatus.Invalid -> {
                sessionManager.clearSession()
                SessionRestoreStatus.Unauthenticated
            }

            SessionValidationStatus.Unavailable -> SessionRestoreStatus.Unavailable
        }
    }
}
