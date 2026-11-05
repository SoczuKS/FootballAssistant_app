package com.soczuks.footballassistant.ui.item.add

sealed class ItemAddStatus {
    object Idle : ItemAddStatus()
    object Pending : ItemAddStatus()
    object Success : ItemAddStatus()
    data class Error(val message: String) : ItemAddStatus()
}
