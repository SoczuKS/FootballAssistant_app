package com.soczuks.footballassistant.api.model.request.auth

data class RefreshTokenRequest(
    private val action: String = "refresh_token", val data: Data
) {
    data class Data(val refreshToken: String)
}
