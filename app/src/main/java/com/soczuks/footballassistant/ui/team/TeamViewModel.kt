package com.soczuks.footballassistant.ui.team

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.AddTeamRequest
import com.soczuks.footballassistant.api.model.request.AddTeamRequestData
import com.soczuks.footballassistant.ui.team.addteamscreen.AddTeamState
import com.soczuks.footballassistant.ui.team.teamlistscreen.TeamListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TeamViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<TeamListUiState>(TeamListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _addTeamState = MutableStateFlow<AddTeamState>(AddTeamState.Idle)
    val addTeamState = _addTeamState.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _uiState.value = TeamListUiState.Loading
            }

            try {
                val response = api.getTeams()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _uiState.value = TeamListUiState.Success(response.body()!!.data.teams)
                    } else {
                        _uiState.value = TeamListUiState.Error(response.body()!!.message)
                    }
                } else {
                    _uiState.value =
                        TeamListUiState.Error(context.getString(R.string.error_load_teams))
                }
            } catch (_: Exception) {
                _uiState.value = TeamListUiState.Error(context.getString(R.string.error_load_teams))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetAddState() {
        _addTeamState.value = AddTeamState.Idle
    }

    fun addTeam(name: String, color: String, competitionId: Int, address: String) {
        viewModelScope.launch {
            _addTeamState.value = AddTeamState.Loading

            try {
                val response =
                    api.addTeam(
                        AddTeamRequest(
                            data = AddTeamRequestData(
                                name, color, competitionId,
                                address.ifBlank { null })
                        )
                    )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _addTeamState.value = AddTeamState.Success
                    } else {
                        _addTeamState.value = AddTeamState.Error(body.message)
                    }
                } else {
                    _addTeamState.value =
                        AddTeamState.Error(context.getString(R.string.error_add_team))
                }
            } catch (_: Exception) {
                _addTeamState.value = AddTeamState.Error(context.getString(R.string.error_add_team))
            }
        }
    }
}
