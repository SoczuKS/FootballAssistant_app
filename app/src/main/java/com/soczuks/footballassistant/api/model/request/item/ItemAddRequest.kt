package com.soczuks.footballassistant.api.model.request.item

data class ItemAddRequest(
    private val action: String = "item_add", val data: Data
) {
    data class Data(val name: String)
}
