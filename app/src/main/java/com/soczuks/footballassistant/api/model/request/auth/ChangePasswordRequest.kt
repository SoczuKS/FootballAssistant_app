package com.soczuks.footballassistant.api.model.request.auth

data class ChangePasswordRequest(private val action: String = "change_password", val data: Data) {
    data class Data(
        val oldPassword: String,
        val newPassword: String
    )
}
