package com.soczuks.footballassistant.ui.team

import android.content.Context
import androidx.lifecycle.ViewModel
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.ui.team.addteamscreen.AddTeamState
import com.soczuks.footballassistant.ui.team.teamlistscreen.TeamListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        // TODO
    }

    fun resetAddState() {
        _addTeamState.value = AddTeamState.Idle
    }

    fun addTeam(name: String, color: String, address: String) {
        // TODO
    }
}
