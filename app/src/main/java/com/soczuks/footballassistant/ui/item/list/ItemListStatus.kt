package com.soczuks.footballassistant.ui.item.list

import com.soczuks.footballassistant.api.model.Item

sealed class ItemListStatus {
    data object Loading : ItemListStatus()
    data class Success(val items: List<Item>) : ItemListStatus()
    data class Error(val message: String) : ItemListStatus()
}
