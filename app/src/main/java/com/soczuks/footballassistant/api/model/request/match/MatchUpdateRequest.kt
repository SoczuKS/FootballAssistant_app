package com.soczuks.footballassistant.api.model.request.match

data class MatchUpdateRequest(private val action: String = "match_update", val data: Data) {
    data class Data(val matchId: Int, val homeScore: Int, val awayScore: Int)
}
