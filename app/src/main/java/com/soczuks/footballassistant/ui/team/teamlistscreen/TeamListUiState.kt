package com.soczuks.footballassistant.ui.team.teamlistscreen

sealed class TeamListUiState {
    data object Loading : TeamListUiState()
    data object Success : TeamListUiState()
    data class Error(val message: String) : TeamListUiState()
}
