package com.soczuks.footballassistant.ui.team

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.Team
import com.soczuks.footballassistant.api.model.request.team.TeamAddRequest
import com.soczuks.footballassistant.data.SessionManager
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TeamViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _listStatus = MutableStateFlow<ApiListStatus<Team>>(ApiListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _addStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val teamAddStatus = _addStatus.asStateFlow()

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    private val _joinStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val joinStatus = _joinStatus.asStateFlow()

    private val _leaveStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val leaveStatus = _leaveStatus.asStateFlow()

    init {
        load()

        viewModelScope.launch {
            _currentUserId.value = sessionManager.getUserId()
        }
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _listStatus.value = ApiListStatus.Loading
            }

            try {
                val response = api.getTeams()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _listStatus.value = ApiListStatus.Success(response.body()!!.data.teams)
                    } else {
                        _listStatus.value = ApiListStatus.Error(response.body()!!.message)
                    }
                } else {
                    _listStatus.value =
                        ApiListStatus.Error(context.getString(R.string.team_load_error))
                }
            } catch (_: Exception) {
                _listStatus.value =
                    ApiListStatus.Error(context.getString(R.string.team_load_error))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetAddStatus() {
        _addStatus.value = ApiActionStatus.Idle
    }

    fun addTeam(name: String, color: String, competitionIds: List<Int>, address: String) {
        viewModelScope.launch {
            _addStatus.value = ApiActionStatus.Pending

            try {
                val response = api.addTeam(
                    TeamAddRequest(
                        data = TeamAddRequest.Data(
                            name, color, competitionIds, address.ifBlank { null })
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _addStatus.value = ApiActionStatus.Success
                    } else {
                        _addStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _addStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.team_add_error))
                }
            } catch (_: Exception) {
                _addStatus.value = ApiActionStatus.Error(context.getString(R.string.team_add_error))
            }
        }
    }

    fun resetJoinStatus() {
        _joinStatus.value = ApiActionStatus.Idle
    }

    fun joinTeam(teamId: Int) {
        viewModelScope.launch {
            _joinStatus.value = ApiActionStatus.Pending

            try {
                val response = api.joinTeam(teamId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _joinStatus.value = ApiActionStatus.Success
                    } else {
                        _joinStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _joinStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.team_join_error))
                }
            } catch (_: Exception) {
                _joinStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.team_join_error))
            }
        }
    }

    fun resetLeaveStatus() {
        _leaveStatus.value = ApiActionStatus.Idle
    }

    fun leaveTeam(teamId: Int) {
        viewModelScope.launch {
            _leaveStatus.value = ApiActionStatus.Pending

            try {
                val response = api.leaveTeam(teamId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _leaveStatus.value = ApiActionStatus.Success
                    } else {
                        _leaveStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _leaveStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.team_leave_error))
                }
            } catch (_: Exception) {
                _leaveStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.team_leave_error))
            }
        }
    }
}
