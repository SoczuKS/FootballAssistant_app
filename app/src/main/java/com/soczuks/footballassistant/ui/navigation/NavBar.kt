package com.soczuks.footballassistant.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SportsSoccer
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
    goToCompetitionsScreen: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.HOME,
            onClick = goToHomeScreen,
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text(stringResource(R.string.home_nav)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.MATCHES,
            onClick = goToMatchesScreen,
            icon = { Icon(Icons.Default.SportsSoccer, null) },
            label = { Text(stringResource(R.string.matches_nav)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.COMPETITIONS,
            onClick = goToCompetitionsScreen,
            icon = { Icon(Trophy, null) },
            label = { Text(stringResource(R.string.competitions_nav)) })
    }
}
