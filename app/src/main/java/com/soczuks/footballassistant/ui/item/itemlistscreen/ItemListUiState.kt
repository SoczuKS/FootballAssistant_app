package com.soczuks.footballassistant.ui.item.itemlistscreen

import com.soczuks.footballassistant.api.model.Item

sealed class ItemListUiState {
    data object Loading : ItemListUiState()
    data class Success(val items: List<Item>) : ItemListUiState()
    data class Error(val message: String) : ItemListUiState()
}
