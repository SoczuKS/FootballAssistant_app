package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.UserProfile

data class UserProfileResponseData(val user: UserProfile)

data class UserProfileResponse(
    val statusCode: Int,
    val message: String,
    val data: UserProfileResponseData
)
