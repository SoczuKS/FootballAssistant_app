package com.soczuks.footballassistant.api.model

data class Team(
    val id: Int,
    val name: String,
    val color: String,
    val address: String? = null,
    val players: List<UserProfile> = emptyList(),
    val matches: List<Match> = emptyList(),
    val competitions: List<Competition> = emptyList()
)
