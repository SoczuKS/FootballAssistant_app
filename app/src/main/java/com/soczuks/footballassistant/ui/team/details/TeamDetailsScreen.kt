package com.soczuks.footballassistant.ui.team.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import com.soczuks.footballassistant.ui.match.list.MatchItem
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar
import com.soczuks.footballassistant.ui.team.TeamViewModel

@Composable
fun TeamDetailsScreen(
    teamId: Int,
    onNavigateBack: () -> Unit,
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToMatchDetailsScreen: (Int) -> Unit,
    teamViewModel: TeamViewModel = hiltViewModel()
) {
    val teamListStatus by teamViewModel.listStatus.collectAsState()
    val teamJoinStatus by teamViewModel.joinStatus.collectAsState()
    val teamLeaveStatus by teamViewModel.leaveStatus.collectAsState()
    val areTeamsRefreshing by teamViewModel.isRefreshing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val team = (teamListStatus as? ApiListStatus.Success)?.data?.find { it.id == teamId }

    val currentUserId by teamViewModel.currentUserId.collectAsState()
    val canJoinTeam =
        currentUserId?.let { userId -> team?.players?.none { it.id == userId } } == true

    LaunchedEffect(teamJoinStatus) {
        when (val status = teamJoinStatus) {
            ApiActionStatus.Success -> {
                teamViewModel.load(fromPullToRefresh = true)
                teamViewModel.resetJoinStatus()
            }

            is ApiActionStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)

                teamViewModel.resetJoinStatus()
            }

            ApiActionStatus.Idle, ApiActionStatus.Pending -> Unit
        }
    }

    LaunchedEffect(teamLeaveStatus) {
        when (val status = teamLeaveStatus) {
            ApiActionStatus.Success -> {
                teamViewModel.load(fromPullToRefresh = true)
                teamViewModel.resetLeaveStatus()
            }

            is ApiActionStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)

                teamViewModel.resetLeaveStatus()
            }

            ApiActionStatus.Idle, ApiActionStatus.Pending -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopBar(title = team?.name ?: "Team", onNavigateBack) },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.TEAMS,
                goToHomeScreen = goToHomeScreen,
                goToMatchesScreen = goToMatchesScreen,
                goToCompetitionsScreen = goToCompetitionsScreen,
                goToItemsScreen = goToItemsScreen,
                goToTeamsScreen = {})
        }) { padding ->
        PullToRefreshBox(
            isRefreshing = areTeamsRefreshing, onRefresh = {
                teamViewModel.load(fromPullToRefresh = true)
            }, modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(stringResource(R.string.team_matches_tab_title)) })
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(stringResource(R.string.team_players_tab_title)) })
                }

                team?.let { team ->
                    when (selectedTabIndex) {
                        0 -> {
                            if (team.matches.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) { Text(text = stringResource(R.string.team_no_matches)) }
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(team.matches) { match ->
                                        MatchItem(match) { goToMatchDetailsScreen(match.id) }
                                    }
                                }
                            }
                        }

                        1 -> {
                            if (canJoinTeam) {
                                Button(
                                    enabled = teamJoinStatus !is ApiActionStatus.Pending,
                                    onClick = {
                                        teamViewModel.joinTeam(team.id)
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    if (teamJoinStatus is ApiActionStatus.Pending) {
                                        CircularProgressIndicator()
                                    } else {
                                        Text(stringResource(R.string.team_join))
                                    }
                                }
                            } else {
                                Button(
                                    enabled = teamLeaveStatus !is ApiActionStatus.Pending,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    onClick = {
                                        teamViewModel.leaveTeam(team.id)
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    if (teamLeaveStatus is ApiActionStatus.Pending) {
                                        CircularProgressIndicator()
                                    } else {
                                        Text(text = stringResource(R.string.team_leave))
                                    }
                                }
                            }

                            if (team.players.isNotEmpty()) {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(team.players) { player -> PlayerItem(player = player) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}