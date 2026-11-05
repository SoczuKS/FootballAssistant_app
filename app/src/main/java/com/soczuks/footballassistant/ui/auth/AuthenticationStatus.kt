package com.soczuks.footballassistant.ui.auth

sealed class AuthenticationStatus {
    object Idle : AuthenticationStatus()
    object Pending : AuthenticationStatus()
    object LoginSuccess : AuthenticationStatus()
    object RegistrationSuccess : AuthenticationStatus()
    data class Error(val message: String) : AuthenticationStatus()
}
