package com.soczuks.footballassistant.ui.home

sealed interface HomeStatus {
    data object Loading : HomeStatus
    data object Success : HomeStatus
    data object Error : HomeStatus
}
