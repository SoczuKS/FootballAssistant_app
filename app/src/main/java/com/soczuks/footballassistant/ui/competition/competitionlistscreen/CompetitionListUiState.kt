package com.soczuks.footballassistant.ui.competition.competitionlistscreen

sealed interface CompetitionListUiState {
    data object Loading : CompetitionListUiState
    data object Success : CompetitionListUiState
    data object Error : CompetitionListUiState
}
