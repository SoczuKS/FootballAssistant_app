package com.soczuks.footballassistant.api.model.request

data class RegisterRequest(
    private val action: String = "register", val data: Data
) {
    data class Data(
        val login: String, val password: String, val email: String
    )
}
