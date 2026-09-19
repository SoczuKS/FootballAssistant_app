package com.soczuks.footballassistant.ui.competition.competitionlistscreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.soczuks.footballassistant.ui.home.HomeUiState
import com.soczuks.footballassistant.ui.home.HomeViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun CompetitionListScreen(
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { TopBar(title = stringResource(R.string.home_title)) },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.HOME,
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
            when (val current = state) {
                HomeUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                HomeUiState.Error -> ErrorContent(onRetry = viewModel::load)
                HomeUiState.Success -> {}
            }
        }
    }
}