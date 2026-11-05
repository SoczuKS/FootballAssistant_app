package com.soczuks.footballassistant.api.model.request.team

data class TeamAddRequest(
    private val action: String = "team_add", val data: Data
) {
    data class Data(
        val name: String,
        val color: String,
        val competitionIds: List<Int>,
        val address: String? = null
    )
}
