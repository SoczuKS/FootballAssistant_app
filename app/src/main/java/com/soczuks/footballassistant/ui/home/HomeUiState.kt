package com.soczuks.footballassistant.ui.home

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Success : HomeUiState
    data object Error : HomeUiState
}