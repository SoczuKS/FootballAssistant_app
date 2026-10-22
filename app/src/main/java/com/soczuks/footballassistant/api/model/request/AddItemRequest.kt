package com.soczuks.footballassistant.api.model.request

data class AddItemRequestData(val name: String)

data class AddItemRequest(
    private val action: String = "item_add",
    val data: AddItemRequestData
)
