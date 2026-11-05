package com.soczuks.footballassistant.api.model.response


data class RefreshTokenResponse(
    val statusCode: Int, val message: String, val data: Data
) {
    data class Data(
        val accessToken: String, val refreshToken: String
    )
}
