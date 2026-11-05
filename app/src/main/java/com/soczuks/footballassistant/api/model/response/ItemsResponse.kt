package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.Item

data class ItemsResponse(
    val statusCode: Int, val message: String, val data: Data
) {
    data class Data(val items: List<Item>)
}
