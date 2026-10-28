package com.soczuks.footballassistant.ui.competition.competitiondetailsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel
import com.soczuks.footballassistant.ui.competition.competitionlistscreen.CompetitionListUiState
import com.soczuks.footballassistant.ui.item.ItemViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar
import com.soczuks.footballassistant.ui.team.teamlistscreen.TeamItem

@Composable
fun CompetitionDetailsScreen(
    competitionId: Int,
    onNavigateBack: () -> Unit,
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit,
    viewModel: CompetitionViewModel = hiltViewModel(),
    itemsViewModel: ItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val areItemsRefreshing by itemsViewModel.isRefreshing.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val competition =
        (uiState as? CompetitionListUiState.Success)?.competitions?.find { it.id == competitionId }

    LaunchedEffect(competitionId) {}

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { TopBar(title = competition?.name ?: "Competition", onNavigateBack) },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.COMPETITIONS,
                goToHomeScreen = goToHomeScreen,
                goToMatchesScreen = goToMatchesScreen,
                goToCompetitionsScreen = {},
                goToItemsScreen = goToItemsScreen,
                goToTeamsScreen = goToTeamsScreen
            )
        }) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing || areItemsRefreshing, onRefresh = {
                viewModel.load(fromPullToRefresh = true)
                itemsViewModel.load(fromPullToRefresh = true)
            }, modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(stringResource(R.string.items_tab_title)) })
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(stringResource(R.string.teams_tab_title)) })
                }

                when (selectedTabIndex) {
                    0 -> {

                    }

                    1 -> {
                        competition?.let { competition ->
                            if (competition.teams.isNotEmpty()) {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(competition.teams) { team ->
                                        TeamItem(
                                            team = team,
                                            onClick = {})
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
