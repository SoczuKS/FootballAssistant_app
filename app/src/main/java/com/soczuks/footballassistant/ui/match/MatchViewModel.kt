package com.soczuks.footballassistant.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.Match
import com.soczuks.footballassistant.ui.common.ApiListStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(private val api: FootballAssistantApi) : ViewModel() {
    private val _listStatus = MutableStateFlow<ApiListStatus<Match>>(ApiListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            _listStatus.value = ApiListStatus.Success<Match>(emptyList())
            _isRefreshing.value = false
        }
    }
}
