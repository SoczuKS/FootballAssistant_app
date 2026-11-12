package com.soczuks.footballassistant.ui.common

sealed class ApiListStatus<out T> {
    data object Loading: ApiListStatus<Nothing>()
    data class Success<T>(val data: List<T>): ApiListStatus<T>()
    data class Error(val message: String): ApiListStatus<Nothing>()
}