package com.soczuks.footballassistant.ui.item.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.model.Item
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.item.ItemViewModel
import com.soczuks.footballassistant.ui.navigation.NavBar
import com.soczuks.footballassistant.ui.navigation.NavBarElement
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun ItemListScreen(
    goToHomeScreen: () -> Unit,
    goToMatchesScreen: () -> Unit,
    goToCompetitionsScreen: () -> Unit,
    goToTeamsScreen: () -> Unit,
    goToItemAddScreen: () -> Unit,
    isRefreshRequired: Boolean = false,
    refreshCallback: () -> Unit = {},
    itemViewModel: ItemViewModel = hiltViewModel()
) {
    val itemListStatus by itemViewModel.listStatus.collectAsState()
    val itemDeleteStatus by itemViewModel.itemDeleteStatus.collectAsState()
    val areItemsRefreshing by itemViewModel.isRefreshing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var itemToDelete by remember { mutableStateOf<Item?>(null) }

    LaunchedEffect(isRefreshRequired) {
        if (isRefreshRequired) {
            itemViewModel.load()
            refreshCallback()
        }
    }

    LaunchedEffect(itemDeleteStatus) {
        if (itemDeleteStatus is ApiActionStatus.Error) {
            snackbarHostState.showSnackbar((itemDeleteStatus as ApiActionStatus.Error).message)
        }
        itemViewModel.resetDeleteStatus()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopBar(title = stringResource(R.string.items_title)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = goToItemAddScreen,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = stringResource(R.string.add_item_title)
                )
            }
        },
        bottomBar = {
            NavBar(
                currentNavBarElement = NavBarElement.ITEMS,
                goToHomeScreen = goToHomeScreen,
                goToMatchesScreen = goToMatchesScreen,
                goToCompetitionsScreen = goToCompetitionsScreen,
                goToItemsScreen = {},
                goToTeamsScreen = goToTeamsScreen
            )
        }) { padding ->
        PullToRefreshBox(
            isRefreshing = areItemsRefreshing,
            onRefresh = { itemViewModel.load(fromPullToRefresh = true) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val status = itemListStatus) {
                ApiListStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ApiListStatus.Error -> ErrorContent(
                    message = status.message, onRetry = itemViewModel::load
                )

                is ApiListStatus.Success -> {
                    if (status.data.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.no_items_found),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(status.data) { item ->
                                ItemItem(
                                    item = item, onDeleteRequest = { itemToDelete = item })
                            }
                        }
                    }
                }
            }
        }
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(stringResource(R.string.item_delete)) },
            text = { Text(stringResource(R.string.item_delete_message, item.name)) },
            confirmButton = {
                TextButton(onClick = {
                    itemViewModel.deleteItem(item.id) {
                        itemToDelete = null
                    }
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    itemToDelete = null
                }) { Text(stringResource(R.string.cancel)) }
            })
    }
}
