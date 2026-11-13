package com.soczuks.footballassistant.ui.competition.list

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
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun CompetitionListScreen(
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit,
    goToCompetitionAddScreen: () -> Unit,
    goToCompetitionDetailsScreen: (Int) -> Unit,
    isRefreshRequired: Boolean = false,
    refreshCallback: () -> Unit = {},
    competitionViewModel: CompetitionViewModel = hiltViewModel()
) {
    val competitionListStatus by competitionViewModel.listStatus.collectAsState()
    val areCompetitionsRefreshing by competitionViewModel.isRefreshing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isRefreshRequired) {
        if (isRefreshRequired) {
            competitionViewModel.load()
            refreshCallback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopBar(title = stringResource(R.string.nav_competitions_title)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = goToCompetitionAddScreen,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.competition_add_title)
                )
            }
        },
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
            isRefreshing = areCompetitionsRefreshing,
            onRefresh = { competitionViewModel.load(fromPullToRefresh = true) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val status = competitionListStatus) {
                ApiListStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ApiListStatus.Error -> ErrorContent(
                    message = status.message, onRetry = competitionViewModel::load
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
                                text = stringResource(R.string.competition_not_found),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(status.data) { competition ->
                                CompetitionItem(
                                    competition = competition,
                                    onClick = { goToCompetitionDetailsScreen(competition.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
