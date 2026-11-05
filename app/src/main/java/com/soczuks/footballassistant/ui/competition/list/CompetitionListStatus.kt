package com.soczuks.footballassistant.ui.competition.list

import com.soczuks.footballassistant.api.model.Competition

sealed class CompetitionListStatus {
    data object Loading : CompetitionListStatus()
    data class Success(val competitions: List<Competition>) : CompetitionListStatus()
    data class Error(val message: String) : CompetitionListStatus()
}
