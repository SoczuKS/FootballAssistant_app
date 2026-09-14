package com.soczuks.footballassistant.api

sealed interface TokenRefreshResult {
    data class Success(val accessToken: String, val refreshToken: String) : TokenRefreshResult
    data object Rejected : TokenRefreshResult
    data object Unavailable : TokenRefreshResult
}
