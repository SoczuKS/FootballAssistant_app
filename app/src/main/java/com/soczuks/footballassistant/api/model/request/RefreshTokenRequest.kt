package com.soczuks.footballassistant.api.model.request

data class RefreshTokenRequestData(val refreshToken: String)

data class RefreshTokenRequest(
    private val action: String = "refresh_token",
    val data: RefreshTokenRequestData
)
