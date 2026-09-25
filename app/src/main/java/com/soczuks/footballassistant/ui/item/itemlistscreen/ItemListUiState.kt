package com.soczuks.footballassistant.ui.item.itemlistscreen

sealed class ItemListUiState {
    data object Loading : ItemListUiState()
    data object Success : ItemListUiState()
    data class Error(val message: String) : ItemListUiState()
}