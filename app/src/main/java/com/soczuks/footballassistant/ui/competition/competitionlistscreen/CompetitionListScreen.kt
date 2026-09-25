package com.soczuks.footballassistant.ui.competition.competitionlistscreen

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
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun CompetitionListScreen(
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    onAddCompetition: () -> Unit,
    onCompetitionClick: (Int) -> Unit,
    isRefreshRequired: Boolean = false,
    refreshCallback: () -> Unit = {},
    viewModel: CompetitionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isRefreshRequired) {
        if (isRefreshRequired) {
            viewModel.load()
            refreshCallback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { TopBar(title = stringResource(R.string.competitions_title)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCompetition,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_competition_button_description)
                )
            }
        },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.COMPETITIONS,
                goToHomeScreen = goToHomeScreen,
                goToMatchesScreen = goToMatchesScreen,
                goToCompetitionsScreen = {}
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.load(fromPullToRefresh = true) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                CompetitionListUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is CompetitionListUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = viewModel::load
                )

                is CompetitionListUiState.Success -> {
                    if (state.competitions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.no_competitions_found),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.competitions) { competition ->
                                CompetitionItem(
                                    competition = competition,
                                    onClick = { onCompetitionClick(competition.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
