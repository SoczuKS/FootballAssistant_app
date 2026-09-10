package com.soczuks.footballassistant.auth

interface SessionValidator {
    suspend fun validate(): SessionValidationResult
}