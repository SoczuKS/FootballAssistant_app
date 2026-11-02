package com.soczuks.footballassistant.ui.competition.competitiondetailsscreen

sealed class CompetitionItemAssignmentState {
    data object Idle : CompetitionItemAssignmentState()
    data object Pending : CompetitionItemAssignmentState()
    data object Success : CompetitionItemAssignmentState()
    data class Error(val message: String) : CompetitionItemAssignmentState()
}
