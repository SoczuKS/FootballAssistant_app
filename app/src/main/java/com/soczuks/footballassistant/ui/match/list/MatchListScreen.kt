package com.soczuks.footballassistant.ui.match.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.soczuks.footballassistant.ui.common.ApiListStatus
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.match.MatchViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar
import java.time.Instant

@Composable
fun MatchListScreen(
    goToHomeScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit,
    goToMatchAddScreen: () -> Unit,
    goToMatchDetailsScreen: (Int) -> Unit,
    isRefreshRequired: Boolean,
    refreshCallback: () -> Unit,
    matchViewModel: MatchViewModel = hiltViewModel()
) {
    val matchListStatus by matchViewModel.listStatus.collectAsState()
    val areMatchesRefreshing by matchViewModel.isRefreshing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(isRefreshRequired) {
        if (isRefreshRequired) {
            matchViewModel.load()
            refreshCallback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopBar(title = stringResource(R.string.nav_matches_title)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = goToMatchAddScreen,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.match_add_button_description)
                )
            }
        },
        bottomBar = {
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
            Column(modifier = Modifier.fillMaxSize()) {
                SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(stringResource(R.string.match_past_tab_title)) })
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(stringResource(R.string.match_future_tab_title)) })
                }

                when (val status = matchListStatus) {
                    ApiListStatus.Loading -> Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center)
                        )
                    }

                    is ApiListStatus.Error -> ErrorContent(
                        message = "", onRetry = matchViewModel::load
                    )

                    is ApiListStatus.Success -> {
                        when (selectedTabIndex) {
                            0 -> {
                                val pastMatches =
                                    status.data.filter { it.matchDate < Instant.now() }

                                if (pastMatches.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) { Text(text = stringResource(R.string.match_no_past_matches)) }
                                } else {
                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(pastMatches) { match ->
                                            MatchItem(match) { goToMatchDetailsScreen(match.id) }
                                        }
                                    }
                                }
                            }

                            1 -> {
                                val futureMatches =
                                    status.data.filter { it.matchDate >= Instant.now() }

                                if (futureMatches.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) { Text(text = stringResource(R.string.match_no_future_matches)) }
                                } else {
                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(futureMatches) { match ->
                                            MatchItem(match) { goToMatchDetailsScreen(match.id) }
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
}
