package com.soczuks.footballassistant.ui.team

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.team.TeamAddRequest
import com.soczuks.footballassistant.ui.team.add.TeamAddStatus
import com.soczuks.footballassistant.ui.team.list.TeamListStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TeamViewModel @Inject constructor(
    @ApplicationContext private val context: Context, private val api: FootballAssistantApi
) : ViewModel() {
    private val _listStatus = MutableStateFlow<TeamListStatus>(TeamListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _teamAddStatus = MutableStateFlow<TeamAddStatus>(TeamAddStatus.Idle)
    val teamAddStatus = _teamAddStatus.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _listStatus.value = TeamListStatus.Loading
            }

            try {
                val response = api.getTeams()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _listStatus.value = TeamListStatus.Success(response.body()!!.data.teams)
                    } else {
                        _listStatus.value = TeamListStatus.Error(response.body()!!.message)
                    }
                } else {
                    _listStatus.value =
                        TeamListStatus.Error(context.getString(R.string.error_load_teams))
                }
            } catch (_: Exception) {
                _listStatus.value =
                    TeamListStatus.Error(context.getString(R.string.error_load_teams))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetAddState() {
        _teamAddStatus.value = TeamAddStatus.Idle
    }

    fun addTeam(name: String, color: String, competitionIds: List<Int>, address: String) {
        viewModelScope.launch {
            _teamAddStatus.value = TeamAddStatus.Pending

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
                        _teamAddStatus.value = TeamAddStatus.Success
                    } else {
                        _teamAddStatus.value = TeamAddStatus.Error(body.message)
                    }
                } else {
                    _teamAddStatus.value =
                        TeamAddStatus.Error(context.getString(R.string.error_add_team))
                }
            } catch (_: Exception) {
                _teamAddStatus.value =
                    TeamAddStatus.Error(context.getString(R.string.error_add_team))
            }
        }
    }
}
