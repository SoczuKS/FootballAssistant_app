package com.soczuks.footballassistant.api.model

import java.time.Instant

data class Match(
    val id: Int,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val matchDate: Instant,
    val homeTeam: Team,
    val awayTeam: Team,
    val competition: Competition
)
