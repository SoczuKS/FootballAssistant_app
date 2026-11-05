package com.soczuks.footballassistant.auth

import com.soczuks.footballassistant.api.FootballAssistantApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiSessionValidator @Inject constructor(private val api: FootballAssistantApi) :
    SessionValidator {
    override suspend fun validate(): SessionValidationStatus = try {
        val response = api.getCurrentUser()

        when {
            response.isSuccessful -> response.body()?.data?.user?.id?.let(SessionValidationStatus::Valid)
                ?: SessionValidationStatus.Unavailable

            response.code() == 401 || response.code() == 403 -> SessionValidationStatus.Invalid

            else -> SessionValidationStatus.Unavailable
        }
    } catch (_: Exception) {
        SessionValidationStatus.Unavailable
    }
}
