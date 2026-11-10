package com.soczuks.footballassistant.ui.common

sealed class ApiActionStatus {
    object Idle : ApiActionStatus()
    object Pending : ApiActionStatus()
    object Success : ApiActionStatus()
    data class Error(val message: String) : ApiActionStatus()
}
