package com.soczuks.footballassistant.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context, private val api: FootballAssistantApi
) : ViewModel() {
    private val _loadStatus = MutableStateFlow<UserProfileLoadStatus>(UserProfileLoadStatus.Loading)
    val loadStatus = _loadStatus.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _loadStatus.value = UserProfileLoadStatus.Loading

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
            }
        }
    }
}
