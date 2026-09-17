package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.UserProfile

data class LoginResponseData(val accessToken: String, val refreshToken: String, val user: UserProfile)

data class LoginResponse(val statusCode: Int, val message: String, val data: LoginResponseData)
