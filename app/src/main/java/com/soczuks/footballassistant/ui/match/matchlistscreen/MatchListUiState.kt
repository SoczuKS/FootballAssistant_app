package com.soczuks.footballassistant.ui.match.matchlistscreen

sealed interface MatchListUiState {
    data object Loading : MatchListUiState
    data object Success : MatchListUiState
    data object Error : MatchListUiState
}
