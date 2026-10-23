package com.soczuks.footballassistant.api.model.request

data class AddTeamRequestData(
    val name: String,
    val color: String,
    val competitionId: Int,
    val address: String? = null
)

data class AddTeamRequest(
    private val action: String = "team_add",
    val data: AddTeamRequestData
)
