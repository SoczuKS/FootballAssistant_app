package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.Team

data class TeamsResponseData(val teams: List<Team>)

data class TeamsResponse(
    val statusCode: Int,
    val message: String,
    val data: TeamsResponseData
)
