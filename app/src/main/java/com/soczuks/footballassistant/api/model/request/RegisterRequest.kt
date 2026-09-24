package com.soczuks.footballassistant.api.model.request

data class RegisterRequestData(val login: String, val password: String, val email: String)

data class RegisterRequest(
    private val action: String = "register",
    val data: RegisterRequestData
)
