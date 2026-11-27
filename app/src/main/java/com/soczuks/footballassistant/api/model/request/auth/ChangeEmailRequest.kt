package com.soczuks.footballassistant.api.model.request.auth

data class ChangeEmailRequest(private val action: String = "change_email", val data: Data) {
    data class Data(val newEmail: String)
}
