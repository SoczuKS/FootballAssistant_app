package com.soczuks.footballassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.soczuks.footballassistant.ui.auth.LoginScreen
import com.soczuks.footballassistant.ui.auth.RegisterScreen
import com.soczuks.footballassistant.ui.auth.StartupScreen
import com.soczuks.footballassistant.ui.auth.StartupState
import com.soczuks.footballassistant.ui.auth.StartupViewModel
import com.soczuks.footballassistant.ui.competition.addcompetitionscreen.AddCompetitionScreen
import com.soczuks.footballassistant.ui.competition.competitiondetailsscreen.CompetitionDetailsScreen
import com.soczuks.footballassistant.ui.competition.competitionlistscreen.CompetitionListScreen
import com.soczuks.footballassistant.ui.home.HomeScreen
import com.soczuks.footballassistant.ui.match.addmatchscreen.AddMatchScreen
import com.soczuks.footballassistant.ui.match.matchdetailsscreen.MatchDetailsScreen
import com.soczuks.footballassistant.ui.match.matchlistscreen.MatchListScreen

sealed class Screen(val route: String) {
    object Startup : Screen("startup")

    object Login : Screen("login")
    object Register : Screen("register")

    object Home : Screen("home")

    object Matches : Screen("matches")
    object MatchDetails : Screen("match_details/{matchId}") {
        fun createRoute(matchId: Int) = "match_details/$matchId"
    }

    object AddMatch : Screen("add_match")

    object Competitions : Screen("competitions")
    object CompetitionDetails : Screen("competition_details/{competitionId}") {
        fun createRoute(competitionId: Int) = "competition_details/$competitionId"
    }

    object AddCompetition : Screen("add_competition")
}

@Composable
fun NavGraph(navController: NavHostController, onAuthenticated: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Screen.Startup.route
    ) {
        composable(Screen.Startup.route) {
            val viewModel: StartupViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state) {
                val destination = when (state) {
                    StartupState.Authenticated -> Screen.Home.route
                    StartupState.Unauthenticated -> Screen.Login.route
                    else -> null
                }

                destination?.let {
                    if (state == StartupState.Authenticated) {
                        onAuthenticated()
                    }
                    navController.navigate(it) {
                        popUpTo(Screen.Startup.route) { inclusive = true }
                    }
                }
            }

            StartupScreen(state = state, onRetry = viewModel::restoreSession)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    onAuthenticated()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                })
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                goToMatchesScreen = {
                    navController.navigate(Screen.Matches.route) {
                        launchSingleTop = true
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(Screen.Competitions.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Matches.route) {
            MatchListScreen(
                goToHomeScreen = {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(Screen.Competitions.route) {
                        launchSingleTop = true
                    }
                },
                onAddMatch = { navController.navigate(Screen.AddMatch.route) }
            )
        }
        composable(Screen.MatchDetails.route) {
            MatchDetailsScreen()
        }
        composable(Screen.AddMatch.route) {
            AddMatchScreen()
        }

        composable(Screen.Competitions.route) {
            CompetitionListScreen(
                goToHomeScreen = {
                    navController.navigate(Screen.Home.route) {
                        launchSingleTop = true
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(Screen.Matches.route) {
                        launchSingleTop = true
                    }
                },
                onAddCompetition = { navController.navigate(Screen.AddCompetition.route) }
            )
        }
        composable(Screen.CompetitionDetails.route) {
            CompetitionDetailsScreen()
        }
        composable(Screen.AddCompetition.route) {
            AddCompetitionScreen()
        }
    }
}
