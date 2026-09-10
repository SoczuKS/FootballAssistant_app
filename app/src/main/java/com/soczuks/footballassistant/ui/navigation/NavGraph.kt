package com.soczuks.footballassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Startup : Screen("startup")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Matches : Screen("matches")
    object MatchDetails : Screen("match_details/{matchId}") {
        fun createRoute(matchId: Int) = "match_details/$matchId"
    }
    object Competitions : Screen("competitions")
    object CompetitionDetails : Screen("competition_details/{competitionId}") {
        fun createRoute(competitionId: Int) = "competition_details/$competitionId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Startup.route
    ) {
        composable(Screen.Startup.route) {

        }
        composable(Screen.Login.route) {

        }
        composable(Screen.Register.route) {

        }
        composable(Screen.Home.route) {

        }
        composable(Screen.Matches.route) {

        }
        composable(Screen.MatchDetails.route) {

        }
        composable(Screen.Competitions.route) {

        }
        composable(Screen.CompetitionDetails.route) {

        }
    }
}