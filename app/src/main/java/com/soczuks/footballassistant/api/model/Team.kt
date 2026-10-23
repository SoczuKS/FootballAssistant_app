package com.soczuks.footballassistant.api.model

data class Team(
    val id: Int,
    val name: String,
    val color: String,
    val competitionId: Int,
    val address: String? = null
)
