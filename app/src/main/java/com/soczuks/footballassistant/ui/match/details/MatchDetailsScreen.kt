package com.soczuks.footballassistant.ui.match.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import com.soczuks.footballassistant.ui.match.MatchViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar
import java.time.Instant

@Composable
fun MatchDetailsScreen(
    matchId: Int,
    onNavigateBack: () -> Unit,
    goToHomeScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit,
    matchViewModel: MatchViewModel = hiltViewModel()
) {
    val matchListStatus by matchViewModel.listStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val itemCheckInStatus by matchViewModel.itemCheckInStatus.collectAsState()
    val areMatchesRefreshing by matchViewModel.isRefreshing.collectAsState()
    val matchUpdateStatus by matchViewModel.updateStatus.collectAsState()

    val match = (matchListStatus as? ApiListStatus.Success)?.data?.find { it.id == matchId }
    val title = match?.let { "${it.homeTeam.name} : ${it.awayTeam.name}" } ?: ""

    LaunchedEffect(itemCheckInStatus) {
        when (val status = itemCheckInStatus) {
            ApiActionStatus.Success -> {
                matchViewModel.load(fromPullToRefresh = true)
                matchViewModel.resetCheckInStatus()
            }

            is ApiActionStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)

                matchViewModel.load(fromPullToRefresh = true)
                matchViewModel.resetCheckInStatus()
            }

            ApiActionStatus.Idle, ApiActionStatus.Pending -> Unit
        }
    }

    LaunchedEffect(matchUpdateStatus) {
        when (val status = matchUpdateStatus) {
            ApiActionStatus.Success -> {
                matchViewModel.load(fromPullToRefresh = true)
                matchViewModel.resetUpdateStatus()
            }

            is ApiActionStatus.Error -> {
                snackbarHostState.showSnackbar(status.message)

                matchViewModel.load(fromPullToRefresh = true)
                matchViewModel.resetUpdateStatus()
            }

            ApiActionStatus.Idle, ApiActionStatus.Pending -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        TopBar(
            title = title, onNavigateBack = onNavigateBack
        )
    }, bottomBar = {
        NavBar(
            currentNavBarElement = NavBarElement.MATCHES,
            goToHomeScreen = goToHomeScreen,
            goToMatchesScreen = {},
            goToCompetitionsScreen = goToCompetitionsScreen,
            goToItemsScreen = goToItemsScreen,
            goToTeamsScreen = goToTeamsScreen
        )
    }) { padding ->
        PullToRefreshBox(
            isRefreshing = areMatchesRefreshing,
            onRefresh = { matchViewModel.load(fromPullToRefresh = true) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            match?.let { match ->
                val isFutureMatch = match.matchDate.isAfter(Instant.now())
                if (isFutureMatch) {
                    FutureMatchScreen(match, matchViewModel)
                } else {
                    PastMatchScreen(match, matchViewModel, matchUpdateStatus)
                }
            }
        }
    }
}
