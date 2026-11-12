package com.soczuks.footballassistant.ui.match

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.Match
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi
) : ViewModel() {
    private val _listStatus = MutableStateFlow<ApiListStatus<Match>>(ApiListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _addStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val matchAddStatus = _addStatus.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _listStatus.value = ApiListStatus.Loading
            }

            try {
                val response = api.getMatches()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _listStatus.value = ApiListStatus.Success(response.body()!!.data.matches)
                    } else {
                        _listStatus.value = ApiListStatus.Error(response.body()!!.message)
                    }
                } else {
                    _listStatus.value =
                        ApiListStatus.Error(context.getString(R.string.match_load_error))
                }
            } catch (_: Exception) {
                _listStatus.value =
                    ApiListStatus.Error(context.getString(R.string.match_load_error))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetAddStatus() {
        _addStatus.value = ApiActionStatus.Idle
    }

    fun addMatch() {
        viewModelScope.launch {

        }
    }
}
