package com.soczuks.footballassistant.api.model.request

data class LoginRequest(
    private val action: String = "login", val data: Data
) {
    data class Data(
        val login: String, val password: String
    )
}
