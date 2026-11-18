package com.soczuks.footballassistant.ui.match

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.Match
import com.soczuks.footballassistant.api.model.request.match.MatchAddRequest
import com.soczuks.footballassistant.api.model.request.match.MatchItemCheckInRequest
import com.soczuks.footballassistant.api.model.request.match.MatchUpdateRequest
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    @ApplicationContext private val context: Context, private val api: FootballAssistantApi
) : ViewModel() {
    private val _listStatus = MutableStateFlow<ApiListStatus<Match>>(ApiListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _addStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val matchAddStatus = _addStatus.asStateFlow()

    private val _checkInStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val itemCheckInStatus = _checkInStatus.asStateFlow()

    private val _updateStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val updateStatus = _updateStatus.asStateFlow()

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

    fun addMatch(competitionId: Int, homeTeamId: Int, awayTeamId: Int, matchDate: Instant) {
        viewModelScope.launch {
            _addStatus.value = ApiActionStatus.Pending

            try {
                val response = api.addMatch(
                    MatchAddRequest(
                        data = MatchAddRequest.Data(
                            homeTeamId, awayTeamId, matchDate, competitionId
                        )
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _addStatus.value = ApiActionStatus.Success
                    } else {
                        _addStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _addStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.match_add_error))
                }
            } catch (_: Exception) {
                _addStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.match_add_error))
            }
        }
    }

    fun resetCheckInStatus() {
        _checkInStatus.value = ApiActionStatus.Idle
    }

    fun checkInItem(matchId: Int, itemId: Int, checkedIn: Boolean) {
        viewModelScope.launch {
            _checkInStatus.value = ApiActionStatus.Pending

            try {
                val response = api.checkInItemForMatch(
                    MatchItemCheckInRequest(
                        data = MatchItemCheckInRequest.Data(
                            matchId, itemId, checkedIn
                        )
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _checkInStatus.value = ApiActionStatus.Success
                    } else {
                        _checkInStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _checkInStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.match_check_in_error))
                }
            } catch (_: Exception) {
                _checkInStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.match_check_in_error))
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = ApiActionStatus.Idle
    }

    fun updateMatch(matchId: Int, homeScore: Int, awayScore: Int) {
        viewModelScope.launch {
            _updateStatus.value = ApiActionStatus.Pending

            try {
                val response = api.updateMatch(
                    MatchUpdateRequest(
                        data = MatchUpdateRequest.Data(
                            matchId, homeScore, awayScore
                        )
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _updateStatus.value = ApiActionStatus.Success
                    } else {
                        _updateStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _updateStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.match_update_error))
                }
            } catch (_: Exception) {
                _updateStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.match_update_error))
            }
        }
    }
}
