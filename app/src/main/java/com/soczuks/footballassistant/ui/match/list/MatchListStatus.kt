package com.soczuks.footballassistant.ui.match.list

sealed interface MatchListStatus {
    data object Loading : MatchListStatus
    data object Success : MatchListStatus
    data object Error : MatchListStatus
}
