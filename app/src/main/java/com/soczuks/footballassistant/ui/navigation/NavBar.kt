package com.soczuks.footballassistant.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.icons.Trophy

@Composable
fun NavBar(
    currentNavBarElement: NavBarElement,
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToItemsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.COMPETITIONS,
            onClick = goToCompetitionsScreen,
            icon = { Icon(Trophy, null) },
            label = { Text(stringResource(R.string.competitions_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.TEAMS,
            onClick = goToTeamsScreen,
            icon = { Icon(Icons.Default.People, null) },
            label = { Text(stringResource(R.string.teams_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.HOME,
            onClick = goToHomeScreen,
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text(stringResource(R.string.home_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.MATCHES,
            onClick = goToMatchesScreen,
            icon = { Icon(Icons.Default.SportsSoccer, null) },
            label = { Text(stringResource(R.string.matches_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.ITEMS,
            onClick = goToItemsScreen,
            icon = { Icon(Icons.Default.Warehouse, null) },
            label = { Text(stringResource(R.string.items_title)) })

    }
}
