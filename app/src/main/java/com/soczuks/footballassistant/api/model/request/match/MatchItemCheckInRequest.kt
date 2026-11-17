package com.soczuks.footballassistant.api.model.request.match

data class MatchItemCheckInRequest(
    private val action: String = "match_check_in_item", val data: Data
) {
    data class Data(val matchId: Int, val itemId: Int, val checkIn: Boolean)
}
