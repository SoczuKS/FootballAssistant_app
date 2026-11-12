package com.soczuks.footballassistant.ui.competition.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.model.Competition
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel
import com.soczuks.footballassistant.ui.item.ItemViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar
import com.soczuks.footballassistant.ui.team.list.TeamItem

@Composable
fun CompetitionDetailsScreen(
    competitionId: Int,
    onNavigateBack: () -> Unit,
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit,
    competitionViewModel: CompetitionViewModel = hiltViewModel(),
    itemsViewModel: ItemViewModel = hiltViewModel()
) {
    val competitionListStatus by competitionViewModel.listStatus.collectAsState()
    val areCompetitionsRefreshing by competitionViewModel.isRefreshing.collectAsState()
    val itemAssignmentStatus by competitionViewModel.itemAssignmentStatus.collectAsState()
    val itemListStatus by itemsViewModel.listStatus.collectAsState()
    val areItemsRefreshing by itemsViewModel.isRefreshing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val competition =
        (competitionListStatus as? ApiListStatus.Success<Competition>)?.data?.find { it.id == competitionId }

    LaunchedEffect(itemAssignmentStatus) {
        when (val status = itemAssignmentStatus) {
            ApiActionStatus.Success -> {
                competitionViewModel.load(fromPullToRefresh = true)
                competitionViewModel.resetItemAssignmentStatus()
            }

            is ApiActionStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)

                competitionViewModel.load(fromPullToRefresh = true)
                competitionViewModel.resetItemAssignmentStatus()
            }

            ApiActionStatus.Idle, ApiActionStatus.Pending -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            isRefreshing = areCompetitionsRefreshing || areItemsRefreshing, onRefresh = {
                competitionViewModel.load(fromPullToRefresh = true)
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
                        when (val status = itemListStatus) {
                            ApiListStatus.Loading -> Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }

                            is ApiListStatus.Error -> ErrorContent(
                                message = status.message, onRetry = itemsViewModel::load
                            )

                            is ApiListStatus.Success -> {
                                if (status.data.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_items_found),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                } else {
                                    competition?.let { competition ->
                                        LazyColumn(
                                            contentPadding = PaddingValues(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            items(status.data) { item ->
                                                CompetitionItemAssignmentItem(
                                                    item,
                                                    competition.itemList.items.any { it.id == item.id },
                                                    onCheckedChange = {
                                                        competitionViewModel.assignItem(
                                                            competition.id, item.id, it
                                                        )
                                                    })
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
                                            team = team, onClick = {})
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
