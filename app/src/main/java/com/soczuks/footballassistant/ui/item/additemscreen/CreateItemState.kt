package com.soczuks.footballassistant.ui.item.additemscreen

sealed class CreateItemState {
    object Idle : CreateItemState()
    object Loading : CreateItemState()
    object Success : CreateItemState()
    data class Error(val message: String) : CreateItemState()
}
