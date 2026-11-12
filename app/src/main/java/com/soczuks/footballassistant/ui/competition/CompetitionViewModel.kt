package com.soczuks.footballassistant.ui.competition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.Competition
import com.soczuks.footballassistant.api.model.request.competition.CompetitionAddRequest
import com.soczuks.footballassistant.api.model.request.competition.CompetitionItemAssignmentRequest
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompetitionViewModel @Inject constructor(
    @ApplicationContext private val context: Context, private val api: FootballAssistantApi
) : ViewModel() {
    private val _listStatus = MutableStateFlow<ApiListStatus<Competition>>(ApiListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _competitionAddStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val competitionAddStatus = _competitionAddStatus.asStateFlow()

    private val _itemAssignmentStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val itemAssignmentStatus = _itemAssignmentStatus.asStateFlow()

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
                val response = api.getCompetitions()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _listStatus.value =
                            ApiListStatus.Success(response.body()!!.data.competitions)
                    } else {
                        _listStatus.value = ApiListStatus.Error(response.body()!!.message)
                    }
                } else {
                    _listStatus.value =
                        ApiListStatus.Error(context.getString(R.string.error_load_competitions))
                }
            } catch (_: Exception) {
                _listStatus.value =
                    ApiListStatus.Error(context.getString(R.string.error_load_competitions))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetAddStatus() {
        _competitionAddStatus.value = ApiActionStatus.Idle
    }

    fun resetItemAssignmentStatus() {
        _itemAssignmentStatus.value = ApiActionStatus.Idle
    }

    fun addCompetition(name: String) {
        viewModelScope.launch {
            _competitionAddStatus.value = ApiActionStatus.Pending
            try {
                val response =
                    api.addCompetition(CompetitionAddRequest(data = CompetitionAddRequest.Data(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _competitionAddStatus.value = ApiActionStatus.Success
                    } else {
                        _competitionAddStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _competitionAddStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.error_add_competitions))
                }
            } catch (_: Exception) {
                _competitionAddStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.error_add_competitions))
            }
        }
    }

    fun assignItem(competitionId: Int, itemId: Int, assignment: Boolean) {
        viewModelScope.launch {
            _itemAssignmentStatus.value = ApiActionStatus.Pending
            try {
                val response = api.assignItemToCompetition(
                    CompetitionItemAssignmentRequest(
                        data = CompetitionItemAssignmentRequest.Data(
                            competitionId, itemId, assignment
                        )
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _itemAssignmentStatus.value = ApiActionStatus.Success
                    } else {
                        _itemAssignmentStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _itemAssignmentStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.error_item_assignment))
                }
            } catch (_: Exception) {
                _itemAssignmentStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.error_item_assignment))
            }
        }
    }
}
