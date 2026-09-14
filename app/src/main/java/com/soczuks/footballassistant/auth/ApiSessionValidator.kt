package com.soczuks.footballassistant.auth

import com.soczuks.footballassistant.api.FootballAssistantApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiSessionValidator @Inject constructor(private val api: FootballAssistantApi) :
    SessionValidator {
    override suspend fun validate(): SessionValidationResult = try {
        val response = api.getCurrentUser()

        when {
            response.isSuccessful -> response.body()?.user?.id?.let(SessionValidationResult::Valid)
                ?: SessionValidationResult.Unavailable

            response.code() == 401 || response.code() == 403 -> SessionValidationResult.Invalid

            else -> SessionValidationResult.Unavailable
        }
    } catch (_: Exception) {
        SessionValidationResult.Unavailable
    }
}
