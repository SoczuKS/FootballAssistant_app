package com.soczuks.footballassistant.api.model.request.competition

data class CompetitionAddRequest(
    private val action: String = "competition_add", val data: Data
) {
    data class Data(val name: String)
}
