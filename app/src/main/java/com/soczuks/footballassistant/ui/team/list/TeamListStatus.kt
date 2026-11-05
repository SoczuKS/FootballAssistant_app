package com.soczuks.footballassistant.ui.team.list

import com.soczuks.footballassistant.api.model.Team

sealed class TeamListStatus {
    data object Loading : TeamListStatus()
    data class Success(val teams: List<Team>) : TeamListStatus()
    data class Error(val message: String) : TeamListStatus()
}
