package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.Competition

data class CompetitionsResponseData(val competitions: List<Competition>)

data class CompetitionsResponse(
    val statusCode: Int,
    val message: String,
    val data: CompetitionsResponseData
)
