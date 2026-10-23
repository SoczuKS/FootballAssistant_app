package com.soczuks.footballassistant.ui.team.teamlistscreen

import com.soczuks.footballassistant.api.model.Team

sealed class TeamListUiState {
    data object Loading : TeamListUiState()
    data class Success(val teams: List<Team>) : TeamListUiState()
    data class Error(val message: String) : TeamListUiState()
}
