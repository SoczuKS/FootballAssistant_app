package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.Match

data class MatchesResponse(val statusCode: Int, val message: String, val data: Data) {
    data class Data(val matches: List<Match>)
}
