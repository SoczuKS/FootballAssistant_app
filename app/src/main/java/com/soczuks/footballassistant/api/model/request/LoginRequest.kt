package com.soczuks.footballassistant.api.model.request

data class LoginRequestData(val login: String, val password: String)

data class LoginRequest(
    private val action: String = "login",
    val data: LoginRequestData
)
