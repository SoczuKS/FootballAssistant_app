package com.soczuks.footballassistant.ui.team.teamlistscreen

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.ui.team.TeamViewModel

@Composable
fun TeamListScreen(
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    onAddTeam: () -> Unit,
    onTeamClick: (Int) -> Unit,
    isRefreshRequired: Boolean = false,
    refreshCallback: () -> Unit = {},
    viewModel: TeamViewModel = hiltViewModel()
) {

}