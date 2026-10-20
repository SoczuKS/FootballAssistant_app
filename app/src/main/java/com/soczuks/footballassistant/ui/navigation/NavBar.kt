package com.soczuks.footballassistant.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.icons.Trophy

@Composable
private fun NavBarIcon(imageVector: ImageVector, selected: Boolean) {
    val transition = updateTransition(targetState = selected, label = "navBarIcon")

    val scale = transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "iconScale"
    ) { isSelected ->
        if (isSelected) 1.25f else 1f
    }

    Icon(imageVector = imageVector, contentDescription = null, modifier = Modifier.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    })
}

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
            icon = {
                NavBarIcon(
                    imageVector = Trophy,
                    selected = currentNavBarElement == NavBarElement.COMPETITIONS
                )
            },
            label = { Text(stringResource(R.string.competitions_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.TEAMS,
            onClick = goToTeamsScreen,
            icon = {
                NavBarIcon(
                    imageVector = Icons.Default.People,
                    selected = currentNavBarElement == NavBarElement.TEAMS
                )
            },
            label = { Text(stringResource(R.string.teams_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.HOME,
            onClick = goToHomeScreen,
            icon = {
                NavBarIcon(
                    imageVector = Icons.Default.Home,
                    selected = currentNavBarElement == NavBarElement.HOME
                )
            },
            label = { Text(stringResource(R.string.home_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.MATCHES,
            onClick = goToMatchesScreen,
            icon = {
                NavBarIcon(
                    imageVector = Icons.Default.SportsSoccer,
                    selected = currentNavBarElement == NavBarElement.MATCHES
                )
            },
            label = { Text(stringResource(R.string.matches_title)) })
        NavigationBarItem(
            selected = currentNavBarElement == NavBarElement.ITEMS,
            onClick = goToItemsScreen,
            icon = {
                NavBarIcon(
                    imageVector = Icons.Default.Warehouse,
                    selected = currentNavBarElement == NavBarElement.ITEMS
                )
            },
            label = { Text(stringResource(R.string.items_title)) })

    }
}
