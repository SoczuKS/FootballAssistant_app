package com.soczuks.footballassistant.api.model.request.competition

data class CompetitionItemAssignmentRequest(
    private val action: String = "competition_assign_item", val data: Data
) {
    data class Data(
        val competitionId: Int, val itemId: Int, val assign: Boolean
    )
}
