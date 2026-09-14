package com.soczuks.footballassistant.api.model.request

data class RegisterRequest(
    val login: String,
    val password: String,
    val email: String
)
