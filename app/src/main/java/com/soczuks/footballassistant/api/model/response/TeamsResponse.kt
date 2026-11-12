package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.Team

data class TeamsResponse(val statusCode: Int, val message: String, val data: Data) {
    data class Data(val teams: List<Team>)
}
