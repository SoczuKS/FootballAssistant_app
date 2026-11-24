package com.soczuks.footballassistant.ui.navigation

import com.soczuks.footballassistant.ui.match.details.MatchDetailsScreenOriginator
import kotlinx.serialization.Serializable

@Serializable
data object StartupDestination

@Serializable
data object LoginDestination

@Serializable
data object RegisterDestination

@Serializable
data object HomeDestination

@Serializable
data object ProfileDestination

@Serializable
data object MatchesDestination

@Serializable
data class MatchDetailsDestination(val matchId: Int, val originator: MatchDetailsScreenOriginator)

@Serializable
data object AddMatchDestination

@Serializable
data object CompetitionsDestination

@Serializable
data class CompetitionDetailsDestination(val competitionId: Int)

@Serializable
data object AddCompetitionDestination

@Serializable
data object ItemsDestination

@Serializable
data object AddItemDestination

@Serializable
data object TeamsDestination

@Serializable
data class TeamDetailsDestination(val teamId: Int)

@Serializable
data object AddTeamDestination
