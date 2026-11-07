package com.soczuks.footballassistant.ui.item.list

sealed class ItemDeleteStatus {
    data object Idle : ItemDeleteStatus()
    data object Pending : ItemDeleteStatus()
    data object Success : ItemDeleteStatus()
    data class Error(val message: String) : ItemDeleteStatus()
}
