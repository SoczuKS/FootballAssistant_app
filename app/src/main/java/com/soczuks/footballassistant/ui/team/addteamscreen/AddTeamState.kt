package com.soczuks.footballassistant.ui.team.addteamscreen

sealed class AddTeamState {
    object Idle : AddTeamState()
    object Loading : AddTeamState()
    object Success : AddTeamState()
    data class Error(val message: String) : AddTeamState()
}
