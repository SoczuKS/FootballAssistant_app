package com.soczuks.footballassistant.api.model.request

data class CompetitionItemAssignmentRequestData(
    val competitionId: Int,
    val itemId: Int,
    val assign: Boolean
)

data class CompetitionItemAssignmentRequest(
    private val action: String = "competition_assign_item",
    val data: CompetitionItemAssignmentRequestData
)
