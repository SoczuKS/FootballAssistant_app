package com.soczuks.footballassistant.api.model

data class UserProfile(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val email: String,
    val login: String
)
