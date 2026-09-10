package com.soczuks.footballassistant.auth

import com.soczuks.footballassistant.api.FootballAssistantApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiSessionValidator @Inject constructor(private val api: FootballAssistantApi) :
    SessionValidator {
    override suspend fun validate(): SessionValidationResult {
        // TODO: Implement API call to validate session and return appropriate SessionValidationResult
        return SessionValidationResult.Invalid
    }
}