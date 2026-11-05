package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.UserProfile


data class LoginResponse(
    val statusCode: Int, val message: String, val data: Data
) {
    data class Data(
        val accessToken: String, val refreshToken: String, val user: UserProfile
    )
}
