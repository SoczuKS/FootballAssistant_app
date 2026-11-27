package com.soczuks.footballassistant.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.UserProfile
import com.soczuks.footballassistant.api.model.request.auth.ChangeEmailRequest
import com.soczuks.footballassistant.api.model.request.auth.ChangePasswordRequest
import com.soczuks.footballassistant.api.model.request.auth.ChangeUserDataRequest
import com.soczuks.footballassistant.data.SessionManager
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserProfileLoadStatus {
    data object Loading : UserProfileLoadStatus()
    data class Success(val data: UserProfile) : UserProfileLoadStatus()
    data class Error(val message: String) : UserProfileLoadStatus()
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _loadStatus = MutableStateFlow<UserProfileLoadStatus>(UserProfileLoadStatus.Loading)
    val loadStatus = _loadStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _changeEmailStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val changeEmailStatus = _changeEmailStatus.asStateFlow()

    private val _changePasswordStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val changePasswordStatus = _changePasswordStatus.asStateFlow()

    private val _changeUserDataStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val changeUserDataStatus = _changeUserDataStatus.asStateFlow()

    private val _logoutStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val logoutStatus = _logoutStatus.asStateFlow()

    init {
        load()
    }

    fun load(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _isRefreshing.value = true
            } else {
                _loadStatus.value = UserProfileLoadStatus.Loading
            }

            try {
                val response = api.getCurrentUser()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _loadStatus.value = UserProfileLoadStatus.Success(body.data.user)
                    } else {
                        _loadStatus.value = UserProfileLoadStatus.Error(body.message)
                    }
                } else {
                    _loadStatus.value =
                        UserProfileLoadStatus.Error(context.getString(R.string.profile_load_error))
                }
            } catch (_: Exception) {
                _loadStatus.value =
                    UserProfileLoadStatus.Error(context.getString(R.string.profile_load_error))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun changeEmail(email: String) {
        viewModelScope.launch {
            try {
                _changeEmailStatus.value = ApiActionStatus.Pending

                val response =
                    api.changeEmail(ChangeEmailRequest(data = ChangeEmailRequest.Data(email)))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _changeEmailStatus.value = ApiActionStatus.Success
                    } else {
                        _changeEmailStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _changeEmailStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.change_email_error))
                }
            } catch (_: Exception) {
                _changeEmailStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.change_email_error))
            }
        }
    }

    fun resetChangeEmailStatus() {
        _changeEmailStatus.value = ApiActionStatus.Idle
    }

    fun changePassword(oldPassword: String, password: String) {
        viewModelScope.launch {
            try {
                _changePasswordStatus.value = ApiActionStatus.Pending

                val response = api.changePassword(
                    ChangePasswordRequest(
                        data = ChangePasswordRequest.Data(
                            oldPassword = oldPassword,
                            newPassword = password
                        )
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _changePasswordStatus.value = ApiActionStatus.Success
                    } else {
                        _changePasswordStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _changePasswordStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.change_password_error))
                }
            } catch (_: Exception) {
                _changePasswordStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.change_password_error))
            }
        }
    }

    fun resetChangePasswordStatus() {
        _changePasswordStatus.value = ApiActionStatus.Idle
    }

    fun changeUserData(firstname: String, lastname: String) {
        viewModelScope.launch {
            try {
                _changeUserDataStatus.value = ApiActionStatus.Pending

                val response = api.changeUserData(
                    ChangeUserDataRequest(
                        data = ChangeUserDataRequest.Data(
                            firstname, lastname
                        )
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _changeUserDataStatus.value = ApiActionStatus.Success
                    } else {
                        _changeUserDataStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _changeUserDataStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.change_user_data_error))
                }
            } catch (_: Exception) {
                _changeUserDataStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.change_user_data_error))
            }
        }
    }

    fun resetChangeUserDataStatus() {
        _changeUserDataStatus.value = ApiActionStatus.Idle
    }

    fun logout() {
        if (_logoutStatus.value is ApiActionStatus.Pending ||
            _logoutStatus.value is ApiActionStatus.Success
        ) {
            return
        }

        viewModelScope.launch {
            _logoutStatus.value = ApiActionStatus.Pending
            try {
                val response = api.logout()
                if (!response.isSuccessful) {
                    Log.w(
                        "UserProfileViewModel",
                        "Remote logout returned HTTP ${response.code()}"
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.w("UserProfileViewModel", "Remote logout failed; clearing local session", exception)
            } finally {
                sessionManager.clearSession()
            }
            _logoutStatus.value = ApiActionStatus.Success
        }
    }
}
