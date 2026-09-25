package com.soczuks.footballassistant.api.model.request

data class CompetitionAddRequestData(val name: String)

data class CompetitionAddRequest(
    private val action: String = "competition_add",
    val data: CompetitionAddRequestData
)
