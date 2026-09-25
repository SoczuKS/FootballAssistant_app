package com.soczuks.footballassistant.ui.competition.competitionlistscreen

import com.soczuks.footballassistant.api.model.Competition

sealed class CompetitionListUiState {
    data object Loading : CompetitionListUiState()
    data class Success(val competitions: List<Competition>) : CompetitionListUiState()
    data class Error(val message: String) : CompetitionListUiState()
}
