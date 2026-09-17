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
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val response = api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    sessionManager.saveTokens(body.data.accessToken, body.data.refreshToken)
                    sessionManager.saveUserId(body.data.user.id)
                    _uiState.value = AuthState.LoginSuccess
                } else {
                    _uiState.value = AuthState.Error(context.getString(R.string.login_failed))
                }
            } catch (_: Exception) {
                _uiState.value = AuthState.Error(context.getString(R.string.login_failed))
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val response = api.register(request)

                if (response.isSuccessful) {
                    _uiState.value = AuthState.RegistrationSuccess
                } else {
                    _uiState.value =
                        AuthState.Error(context.getString(R.string.registration_failed))
                }
            } catch (_: Exception) {
                _uiState.value = AuthState.Error(context.getString(R.string.registration_failed))
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthState.Idle
    }
}
