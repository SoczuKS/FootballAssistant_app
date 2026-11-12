package com.soczuks.footballassistant.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun HomeScreen(
    goToMatchesScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopBar(title = stringResource(R.string.home_title)) },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.HOME,
                goToHomeScreen = {},
                goToMatchesScreen = goToMatchesScreen,
                goToCompetitionsScreen = goToCompetitionsScreen,
                goToItemsScreen = goToItemsScreen,
                goToTeamsScreen = goToTeamsScreen
            )
        }) { padding ->
        PullToRefreshBox(
            isRefreshing = false, onRefresh = {}, modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {}
    }
}
