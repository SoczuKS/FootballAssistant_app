package com.soczuks.footballassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.soczuks.footballassistant.ui.auth.LoginScreen
import com.soczuks.footballassistant.ui.auth.RegisterScreen
import com.soczuks.footballassistant.ui.auth.StartupScreen
import com.soczuks.footballassistant.ui.auth.StartupStatus
import com.soczuks.footballassistant.ui.auth.StartupViewModel
import com.soczuks.footballassistant.ui.competition.add.CompetitionAddScreen
import com.soczuks.footballassistant.ui.competition.details.CompetitionDetailsScreen
import com.soczuks.footballassistant.ui.competition.list.CompetitionListScreen
import com.soczuks.footballassistant.ui.home.HomeScreen
import com.soczuks.footballassistant.ui.item.add.ItemAddScreen
import com.soczuks.footballassistant.ui.item.list.ItemListScreen
import com.soczuks.footballassistant.ui.match.add.MatchAddScreen
import com.soczuks.footballassistant.ui.match.details.MatchDetailsScreen
import com.soczuks.footballassistant.ui.match.details.MatchDetailsScreenOriginator
import com.soczuks.footballassistant.ui.match.list.MatchListScreen
import com.soczuks.footballassistant.ui.profile.ProfileScreen
import com.soczuks.footballassistant.ui.team.add.TeamAddScreen
import com.soczuks.footballassistant.ui.team.details.TeamDetailsScreen
import com.soczuks.footballassistant.ui.team.list.TeamListScreen

@Composable
fun NavGraph(navController: NavHostController, onAuthenticated: () -> Unit) {
    NavHost(
        navController = navController, startDestination = StartupDestination
    ) {
        composable<StartupDestination> {
            val viewModel: StartupViewModel = hiltViewModel()
            val startupStatus by viewModel.startupStatus.collectAsState()

            LaunchedEffect(startupStatus) {
                val destination = when (startupStatus) {
                    StartupStatus.Authenticated -> HomeDestination
                    StartupStatus.Unauthenticated -> LoginDestination
                    else -> null
                }

                destination?.let {
                    if (startupStatus == StartupStatus.Authenticated) {
                        onAuthenticated()
                    }
                    navController.navigate(it) {
                        popUpTo<StartupDestination> { inclusive = true }
                    }
                }
            }

            StartupScreen(startupStatus = startupStatus, onRetry = viewModel::restoreSession)
        }

        composable<LoginDestination> { backStackEntry ->
            val showLoginRequiredMessage by backStackEntry.savedStateHandle
                .getStateFlow("show_login_required_message", false)
                .collectAsState()
            LoginScreen(
                onNavigateToRegister = { navController.navigate(RegisterDestination) },
                showLoginRequiredMessage = showLoginRequiredMessage,
                onLoginMessageShown = {
                    backStackEntry.savedStateHandle["show_login_required_message"] = false
                },
                onLoginSuccess = {
                    onAuthenticated()
                    navController.navigate(HomeDestination) {
                        popUpTo<LoginDestination> {
                            inclusive = true
                        }
                    }
                })
        }
        composable<RegisterDestination> {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() })
        }

        composable<HomeDestination> {
            HomeScreen(goToMatchesScreen = {
                navController.navigate(MatchesDestination) {
                    popUpTo<HomeDestination> { inclusive = true }
                }
            }, goToCompetitionsScreen = {
                navController.navigate(CompetitionsDestination) {
                    popUpTo<HomeDestination> { inclusive = true }
                }
            }, goToItemsScreen = {
                navController.navigate(ItemsDestination) {
                    popUpTo<HomeDestination> { inclusive = true }
                }
            }, goToTeamsScreen = {
                navController.navigate(TeamsDestination) {
                    popUpTo<HomeDestination> { inclusive = true }
                }
            }, goToProfileScreen = { navController.navigate(ProfileDestination) })
        }

        composable<ProfileDestination> {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = { showLoginRequiredMessage ->
                    navController.navigate(LoginDestination) {
                        popUpTo<HomeDestination> { inclusive = true }
                        launchSingleTop = true
                    }
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "show_login_required_message",
                        showLoginRequiredMessage
                    )
                }
            )
        }

        composable<MatchesDestination> { backStackEntry ->
            val matchCreated by backStackEntry.savedStateHandle.getStateFlow("match_created", false)
                .collectAsState()

            MatchListScreen(
                goToHomeScreen = {
                    navController.navigate(HomeDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(CompetitionsDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(ItemsDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(TeamsDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToMatchAddScreen = { navController.navigate(AddMatchDestination) },
                goToMatchDetailsScreen = { matchId ->
                    navController.navigate(
                        MatchDetailsDestination(
                            matchId, MatchDetailsScreenOriginator.MATCH_LIST_SCREEN
                        )
                    )
                },
                goToProfileScreen = { navController.navigate(ProfileDestination) },
                isRefreshRequired = matchCreated,
                refreshCallback = { backStackEntry.savedStateHandle["match_created"] = false })
        }
        composable<MatchDetailsDestination> { backStackEntry ->
            val route = backStackEntry.toRoute<MatchDetailsDestination>()

            MatchDetailsScreen(
                matchId = route.matchId,
                originator = route.originator,
                onNavigateBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "match_created", true
                    )
                    navController.popBackStack()
                },
                goToHomeScreen = {
                    navController.navigate(HomeDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(CompetitionsDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(ItemsDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(TeamsDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(MatchesDestination) {
                        popUpTo<MatchesDestination> { inclusive = true }
                    }
                })
        }
        composable<AddMatchDestination> {
            MatchAddScreen(onNavigateBack = { navController.popBackStack() }, onMatchCreated = {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "match_created", true
                )
            })
        }

        composable<CompetitionsDestination> { backStackEntry ->
            val competitionCreated by backStackEntry.savedStateHandle.getStateFlow(
                "competition_created", false
            ).collectAsState()

            CompetitionListScreen(
                goToHomeScreen = {
                    navController.navigate(HomeDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(MatchesDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(ItemsDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(TeamsDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                },
                goToCompetitionAddScreen = { navController.navigate(AddCompetitionDestination) },
                goToCompetitionDetailsScreen = { competitionId ->
                    navController.navigate(
                        CompetitionDetailsDestination(
                            competitionId
                        )
                    )
                },
                goToProfileScreen = { navController.navigate(ProfileDestination) },
                isRefreshRequired = competitionCreated,
                refreshCallback = {
                    backStackEntry.savedStateHandle["competition_created"] = false
                })
        }
        composable<CompetitionDetailsDestination> { backStackEntry ->
            val route = backStackEntry.toRoute<CompetitionDetailsDestination>()

            CompetitionDetailsScreen(
                competitionId = route.competitionId,
                onNavigateBack = { navController.popBackStack() },
                goToHomeScreen = {
                    navController.navigate(HomeDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(MatchesDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(ItemsDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(TeamsDestination) {
                        popUpTo<CompetitionsDestination> { inclusive = true }
                    }
                })
        }
        composable<AddCompetitionDestination> {
            CompetitionAddScreen(
                onNavigateBack = { navController.popBackStack() },
                onCompetitionCreated = {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "competition_created", true
                    )
                })
        }

        composable<ItemsDestination> { backStackEntry ->
            val itemCreated by backStackEntry.savedStateHandle.getStateFlow("item_created", false)
                .collectAsState()

            ItemListScreen(
                goToHomeScreen = {
                    navController.navigate(HomeDestination) {
                        popUpTo<ItemsDestination> { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(MatchesDestination) {
                        popUpTo<ItemsDestination> { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(CompetitionsDestination) {
                        popUpTo<ItemsDestination> { inclusive = true }
                    }
                },
                goToTeamsScreen = {
                    navController.navigate(TeamsDestination) {
                        popUpTo<ItemsDestination> { inclusive = true }
                    }
                },
                goToItemAddScreen = { navController.navigate(AddItemDestination) },
                goToProfileScreen = { navController.navigate(ProfileDestination) },
                isRefreshRequired = itemCreated,
                refreshCallback = {
                    backStackEntry.savedStateHandle["item_created"] = false
                })
        }
        composable<AddItemDestination> {
            ItemAddScreen(onNavigateBack = { navController.popBackStack() }, onItemCreated = {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "item_created", true
                )
            })
        }

        composable<TeamsDestination> { backStackEntry ->
            val teamCreated by backStackEntry.savedStateHandle.getStateFlow(
                "team_created", false
            ).collectAsState()

            TeamListScreen(
                goToHomeScreen = {
                    navController.navigate(HomeDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(MatchesDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(CompetitionsDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(ItemsDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToTeamAddScreen = { navController.navigate(AddTeamDestination) },
                goToTeamDetailsScreen = { teamId ->
                    navController.navigate(
                        TeamDetailsDestination(
                            teamId
                        )
                    )
                },
                goToProfileScreen = { navController.navigate(ProfileDestination) },
                isRefreshRequired = teamCreated,
                refreshCallback = { backStackEntry.savedStateHandle["team_created"] = false })
        }
        composable<TeamDetailsDestination> { backStackEntry ->
            val route = backStackEntry.toRoute<TeamDetailsDestination>()

            TeamDetailsScreen(
                teamId = route.teamId,
                onNavigateBack = { navController.popBackStack() },
                goToHomeScreen = {
                    navController.navigate(HomeDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToMatchesScreen = {
                    navController.navigate(MatchesDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToItemsScreen = {
                    navController.navigate(ItemsDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToCompetitionsScreen = {
                    navController.navigate(CompetitionsDestination) {
                        popUpTo<TeamsDestination> { inclusive = true }
                    }
                },
                goToMatchDetailsScreen = { matchId ->
                    navController.navigate(
                        MatchDetailsDestination(
                            matchId, MatchDetailsScreenOriginator.TEAM_DETAILS_SCREEN
                        )
                    )
                })
        }
        composable<AddTeamDestination> {
            TeamAddScreen(onNavigateBack = { navController.popBackStack() }, onTeamCreated = {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "team_created", true
                )
            })
        }
    }
}
