package com.soczuks.footballassistant.api.model.response

import com.soczuks.footballassistant.api.model.Item

data class ItemsResponseData(val items: List<Item>)

data class ItemsResponse(
    val statusCode: Int,
    val message: String,
    val data: ItemsResponseData
)
