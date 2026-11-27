package com.soczuks.footballassistant.api.model.request.auth

data class ChangeUserDataRequest(private val action: String = "change_user_data", val data: Data) {
    data class Data(val firstname: String, val lastname: String)
}
