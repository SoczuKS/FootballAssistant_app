package com.soczuks.footballassistant.ui.team.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.common.ApiListStatus
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar
import com.soczuks.footballassistant.ui.team.TeamViewModel

@Composable
fun TeamListScreen(
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamAddScreen: () -> Unit,
    goToTeamDetailsScreen: (Int) -> Unit,
    isRefreshRequired: Boolean = false,
    refreshCallback: () -> Unit = {},
    teamViewModel: TeamViewModel = hiltViewModel()
) {
    val teamListStatus by teamViewModel.listStatus.collectAsState()
    val areTeamsRefreshing by teamViewModel.isRefreshing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isRefreshRequired) {
        if (isRefreshRequired) {
            teamViewModel.load()
            refreshCallback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopBar(title = stringResource(R.string.nav_teams_title)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = goToTeamAddScreen,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = stringResource(R.string.team_add_title)
                )
            }
        },
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
            isRefreshing = areTeamsRefreshing,
            onRefresh = { teamViewModel.load(fromPullToRefresh = true) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val status = teamListStatus) {
                ApiListStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ApiListStatus.Error -> ErrorContent(
                    message = status.message, onRetry = teamViewModel::load
                )

                is ApiListStatus.Success -> {
                    if (status.data.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.team_not_found),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(status.data) { team ->
                                TeamItem(
                                    team = team, onClick = { goToTeamDetailsScreen(team.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
