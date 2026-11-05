package com.soczuks.footballassistant.ui.competition.add

sealed class CompetitionAddStatus {
    object Idle : CompetitionAddStatus()
    object Pending : CompetitionAddStatus()
    object Success : CompetitionAddStatus()
    data class Error(val message: String) : CompetitionAddStatus()
}
