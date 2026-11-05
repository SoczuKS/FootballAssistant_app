package com.soczuks.footballassistant.api

sealed interface TokenRefreshStatus {
    data class Success(val accessToken: String, val refreshToken: String) : TokenRefreshStatus
    data object Rejected : TokenRefreshStatus
    data object Unavailable : TokenRefreshStatus
}
