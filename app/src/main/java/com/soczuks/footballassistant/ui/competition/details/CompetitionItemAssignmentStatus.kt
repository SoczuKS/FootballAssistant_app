package com.soczuks.footballassistant.ui.competition.details

sealed class CompetitionItemAssignmentStatus {
    data object Idle : CompetitionItemAssignmentStatus()
    data object Pending : CompetitionItemAssignmentStatus()
    data object Success : CompetitionItemAssignmentStatus()
    data class Error(val message: String) : CompetitionItemAssignmentStatus()
}
