package com.soczuks.footballassistant.ui.auth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object LoginSuccess : AuthState()
    object RegistrationSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}
