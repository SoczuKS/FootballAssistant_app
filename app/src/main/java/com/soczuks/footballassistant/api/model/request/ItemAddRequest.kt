package com.soczuks.footballassistant.api.model.request

data class ItemAddRequestData(val name: String)

data class ItemAddRequest(
    private val action: String = "item_add",
    val data: ItemAddRequestData
)
