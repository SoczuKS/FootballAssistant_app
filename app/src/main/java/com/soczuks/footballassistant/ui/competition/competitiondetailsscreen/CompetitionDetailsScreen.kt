package com.soczuks.footballassistant.ui.competition.competitiondetailsscreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel
import com.soczuks.footballassistant.ui.competition.competitionlistscreen.CompetitionListUiState
import com.soczuks.footballassistant.ui.item.ItemViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun CompetitionDetailsScreen(
    competitionId: Int,
    onNavigateBack: () -> Unit,
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    viewModel: CompetitionViewModel = hiltViewModel(),
    itemsViewModel: ItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val areItemsRefreshing by itemsViewModel.isRefreshing.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val competition =
        (uiState as? CompetitionListUiState.Success)?.competitions?.find { it.id == competitionId }

    LaunchedEffect(competitionId) {
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { TopBar(title = competition?.name ?: "Competition", onNavigateBack) },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.COMPETITIONS,
                goToHomeScreen = goToHomeScreen,
                goToMatchesScreen = goToMatchesScreen,
                goToCompetitionsScreen = {},
                goToItemsScreen = goToItemsScreen
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing || areItemsRefreshing,
            onRefresh = {
                viewModel.load(fromPullToRefresh = true)
                itemsViewModel.load(fromPullToRefresh = true)
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) { }
    }
}
