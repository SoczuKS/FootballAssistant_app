package com.soczuks.footballassistant.ui.item

import android.content.Context
import androidx.lifecycle.ViewModel
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.ui.item.itemlistscreen.ItemListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<ItemListUiState>(ItemListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        _uiState.value = ItemListUiState.Success
    }
}