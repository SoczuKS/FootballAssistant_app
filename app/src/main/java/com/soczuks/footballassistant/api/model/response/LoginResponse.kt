package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.UserProfile

data class LoginResponse(val accessToken: String, val refreshToken: String, val user: UserProfile)
