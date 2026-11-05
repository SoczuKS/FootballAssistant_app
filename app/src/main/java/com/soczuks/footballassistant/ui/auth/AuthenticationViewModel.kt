package com.soczuks.footballassistant.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.LoginRequest
import com.soczuks.footballassistant.api.model.request.RegisterRequest
import com.soczuks.footballassistant.data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _authenticationStatus =
        MutableStateFlow<AuthenticationStatus>(AuthenticationStatus.Idle)
    val authenticationStatus = _authenticationStatus.asStateFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authenticationStatus.value = AuthenticationStatus.Pending
            try {
                val response = api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        sessionManager.saveTokens(body.data.accessToken, body.data.refreshToken)
                        sessionManager.saveUserId(body.data.user.id)
                        _authenticationStatus.value = AuthenticationStatus.LoginSuccess
                    } else {
                        _authenticationStatus.value = AuthenticationStatus.Error(body.message)
                    }
                } else {
                    _authenticationStatus.value =
                        AuthenticationStatus.Error(context.getString(R.string.login_failed))
                }
            } catch (_: Exception) {
                _authenticationStatus.value =
                    AuthenticationStatus.Error(context.getString(R.string.login_failed))
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authenticationStatus.value = AuthenticationStatus.Pending
            try {
                val response = api.register(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _authenticationStatus.value = AuthenticationStatus.RegistrationSuccess
                    } else {
                        _authenticationStatus.value = AuthenticationStatus.Error(body.message)
                    }
                } else {
                    _authenticationStatus.value =
                        AuthenticationStatus.Error(context.getString(R.string.registration_failed))
                }
            } catch (_: Exception) {
                _authenticationStatus.value =
                    AuthenticationStatus.Error(context.getString(R.string.registration_failed))
            }
        }
    }

    fun resetState() {
        _authenticationStatus.value = AuthenticationStatus.Idle
    }
}
