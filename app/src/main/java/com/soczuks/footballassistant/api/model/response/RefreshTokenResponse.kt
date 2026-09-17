package com.soczuks.footballassistant.api.model.response

data class RefreshTokenResponseData(val accessToken: String, val refreshToken: String)

data class RefreshTokenResponse(val statusCode: Int, val message: String, val data: RefreshTokenResponseData)
