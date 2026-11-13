package com.soczuks.footballassistant.api.model.request.match

import java.time.Instant

data class MatchAddRequest(private val action: String = "match_add", val data: Data) {
    data class Data(
        val homeTeamId: Int, val awayTeamId: Int, val matchDate: Instant, val competitionId: Int
    )
}
