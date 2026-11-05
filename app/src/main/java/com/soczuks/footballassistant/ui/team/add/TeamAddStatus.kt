package com.soczuks.footballassistant.ui.team.add

sealed class TeamAddStatus {
    object Idle : TeamAddStatus()
    object Pending : TeamAddStatus()
    object Success : TeamAddStatus()
    data class Error(val message: String) : TeamAddStatus()
}
