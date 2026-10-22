package com.soczuks.footballassistant.api.model.request

data class AddCompetitionRequestData(val name: String)

data class AddCompetitionRequest(
    private val action: String = "competition_add",
    val data: AddCompetitionRequestData
)
