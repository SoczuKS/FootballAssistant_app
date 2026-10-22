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
import com.soczuks.footballassistant.ui.item.additemscreen.AddItemScreen
import com.soczuks.footballassistant.ui.item.itemlistscreen.ItemListScreen
import com.soczuks.footballassistant.ui.match.addmatchscreen.AddMatchScreen
import com.soczuks.footballassistant.ui.match.matchdetailsscreen.MatchDetailsScreen
import com.soczuks.footballassistant.ui.match.matchlistscreen.MatchListScreen
import com.soczuks.footballassistant.ui.team.addteamscreen.AddTeamScreen
import com.soczuks.footballassistant.ui.team.teamlistscreen.TeamListScreen

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

    object Items : Screen("items")

    object AddItem : Screen("add_item")

    object Teams : Screen("teams")
    object TeamDetails : Screen("team_details/{teamId}") {
        fun createRoute(teamId: Int) = "team_details/$teamId"
    }

    object AddTeam : Screen("add_team")
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
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(Screen.Competitions.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(Screen.Items.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(Screen.Teams.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Matches.route) {
            MatchListScreen(
                goToHomeScreen = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Matches.route) { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(Screen.Competitions.route) {
                        popUpTo(Screen.Matches.route) { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(Screen.Items.route) {
                        popUpTo(Screen.Matches.route) { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(Screen.Teams.route) {
                        popUpTo(Screen.Matches.route) { inclusive = true }
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

        composable(Screen.Competitions.route) { backStackEntry ->
            val competitionCreated by backStackEntry.savedStateHandle.getStateFlow(
                "competition_created",
                false
            ).collectAsState()

            CompetitionListScreen(
                goToHomeScreen = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(Screen.Matches.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(Screen.Items.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(Screen.Teams.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                },
                onAddCompetition = { navController.navigate(Screen.AddCompetition.route) },
                onCompetitionClick = { competitionId ->
                    navController.navigate(
                        Screen.CompetitionDetails.createRoute(
                            competitionId
                        )
                    )
                },
                isRefreshRequired = competitionCreated,
                refreshCallback = {
                    backStackEntry.savedStateHandle["competition_created"] = false
                }
            )
        }
        composable(Screen.CompetitionDetails.route) {
            CompetitionDetailsScreen(
                competitionId = it.arguments?.getString("competitionId")?.toInt() ?: 0,
                onNavigateBack = { navController.popBackStack() },
                goToHomeScreen = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(Screen.Matches.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(Screen.Items.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(Screen.Teams.route) {
                        popUpTo(Screen.Competitions.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AddCompetition.route) {
            AddCompetitionScreen(
                onNavigateBack = { navController.popBackStack() },
                onCompetitionCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "competition_created",
                        true
                    )
                }
            )
        }

        composable(Screen.Items.route) { backStackEntry ->
            val itemCreated by backStackEntry.savedStateHandle.getStateFlow("item_created", false)
                .collectAsState()

            ItemListScreen(
                goToHomeScreen = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Items.route) { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(Screen.Matches.route) {
                        popUpTo(Screen.Items.route) { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(Screen.Competitions.route) {
                        popUpTo(Screen.Items.route) { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(Screen.Teams.route) {
                        popUpTo(Screen.Items.route) { inclusive = true }
                    }
                },
                onAddItem = { navController.navigate(Screen.AddItem.route) },
                isRefreshRequired = itemCreated,
                refreshCallback = {
                    backStackEntry.savedStateHandle["item_created"] = false
                }
            )
        }
        composable(Screen.AddItem.route) {
            AddItemScreen(
                onNavigateBack = { navController.popBackStack() },
                onItemCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "item_created",
                        true
                    )
                })
        }

        composable(Screen.Teams.route) { backStackEntry ->
            val teamCreated by backStackEntry.savedStateHandle.getStateFlow(
                "team_created",
                false
            ).collectAsState()

            TeamListScreen(
                goToHomeScreen = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Teams.route) { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(Screen.Matches.route) {
                        popUpTo(Screen.Teams.route) { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(Screen.Competitions.route) {
                        popUpTo(Screen.Teams.route) { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(Screen.Items.route) {
                        popUpTo(Screen.Teams.route) { inclusive = true }
                    }
                },
                onAddTeam = { navController.navigate(Screen.AddTeam.route) },
                onTeamClick = { teamId ->
                    navController.navigate(
                        Screen.TeamDetails.createRoute(
                            teamId
                        )
                    )
                },
                isRefreshRequired = teamCreated,
                refreshCallback = { backStackEntry.savedStateHandle["team_created"] = false }
            )
        }
        composable(Screen.TeamDetails.route) {

        }
        composable(Screen.AddTeam.route) {
            AddTeamScreen(
                onNavigateBack = { navController.popBackStack() },
                onTeamCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "team_created",
                        true
                    )
                })
        }
    }
}
