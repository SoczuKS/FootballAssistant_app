package com.soczuks.footballassistant.ui.item.additemscreen

sealed class AddItemState {
    object Idle : AddItemState()
    object Loading : AddItemState()
    object Success : AddItemState()
    data class Error(val message: String) : AddItemState()
}
