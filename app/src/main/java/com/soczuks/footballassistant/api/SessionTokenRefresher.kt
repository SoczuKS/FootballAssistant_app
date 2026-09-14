package com.soczuks.footballassistant.api

import com.soczuks.footballassistant.data.SessionManager
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SessionTokenRefresher @Inject constructor(
    private val sessionManager: SessionManager,
    private val gateway: TokenRefreshGateway
) {
    private val refreshLock = Any()

    fun refreshAccessToken(failedAccessToken: String): String? = synchronized(refreshLock) {
        runBlocking {
            val currentAccessToken = try {
                sessionManager.getAccessToken()
            } catch (_: Exception) {
                return@runBlocking null
            }

            if (currentAccessToken != failedAccessToken) {
                return@runBlocking currentAccessToken
            }

            val refreshToken = sessionManager.getRefreshToken()?.takeIf { it.isNotBlank() }
            if (refreshToken == null) {
                sessionManager.clearSession()
                return@runBlocking null
            }

            when (val result = gateway.refresh(refreshToken)) {
                is TokenRefreshResult.Success -> {
                    if (sessionManager.getRefreshToken() != refreshToken) {
                        return@runBlocking null
                    }
                    sessionManager.saveTokens(result.accessToken, result.refreshToken)
                    result.accessToken
                }

                TokenRefreshResult.Rejected -> {
                    sessionManager.clearSession()
                    null
                }

                TokenRefreshResult.Unavailable -> null
            }
        }
    }
}
