package com.soczuks.footballassistant.ui.competition.addcompetitionscreen

sealed class AddCompetitionState {
    object Idle : AddCompetitionState()
    object Loading : AddCompetitionState()
    object Success : AddCompetitionState()
    data class Error(val message: String) : AddCompetitionState()
}
