package com.soczuks.footballassistant.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun HomeScreen(
    goToMatchesScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
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
                goToHomeScreen = {},
                goToMatchesScreen = goToMatchesScreen,
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
                HomeUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                HomeUiState.Error -> ErrorContent(onRetry = viewModel::load)
                HomeUiState.Success -> {}
            }
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.home_error), color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}
