package com.soczuks.footballassistant.ui.competition.addcompetitionscreen

sealed class CreateCompetitionState {
    object Idle : CreateCompetitionState()
    object Loading : CreateCompetitionState()
    object Success : CreateCompetitionState()
    data class Error(val message: String) : CreateCompetitionState()
}