package com.soczuks.footballassistant.ui.match.matchlistscreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun MatchListScreen(
    goToHomeScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    onAddMatch: () -> Unit,
    viewModel: MatchListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { TopBar(title = stringResource(R.string.matches_title)) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMatch, containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_match_button_description))
            }
        },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.HOME,
                goToHomeScreen = goToHomeScreen,
                goToMatchesScreen = {},
                goToCompetitionsScreen = goToCompetitionsScreen
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
            when (val current = state) {
                MatchListUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                MatchListUiState.Error -> ErrorContent(onRetry = viewModel::load)
                MatchListUiState.Success -> {}
            }
        }
    }
}
