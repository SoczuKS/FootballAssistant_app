package com.soczuks.footballassistant.ui.competition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.competition.CompetitionAddRequest
import com.soczuks.footballassistant.api.model.request.competition.CompetitionItemAssignmentRequest
import com.soczuks.footballassistant.ui.competition.add.CompetitionAddStatus
import com.soczuks.footballassistant.ui.competition.details.CompetitionItemAssignmentStatus
import com.soczuks.footballassistant.ui.competition.list.CompetitionListStatus
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
    private val _listStatus = MutableStateFlow<CompetitionListStatus>(CompetitionListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _competitionAddStatus =
        MutableStateFlow<CompetitionAddStatus>(CompetitionAddStatus.Idle)
    val competitionAddStatus = _competitionAddStatus.asStateFlow()

    private val _itemAssignmentStatus =
        MutableStateFlow<CompetitionItemAssignmentStatus>(CompetitionItemAssignmentStatus.Idle)
    val itemAssignmentStatus = _itemAssignmentStatus.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _listStatus.value = CompetitionListStatus.Loading
            }

            try {
                val response = api.getCompetitions()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _listStatus.value =
                            CompetitionListStatus.Success(response.body()!!.data.competitions)
                    } else {
                        _listStatus.value = CompetitionListStatus.Error(response.body()!!.message)
                    }
                } else {
                    _listStatus.value =
                        CompetitionListStatus.Error(context.getString(R.string.error_load_competitions))
                }
            } catch (_: Exception) {
                _listStatus.value =
                    CompetitionListStatus.Error(context.getString(R.string.error_load_competitions))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetAddStatus() {
        _competitionAddStatus.value = CompetitionAddStatus.Idle
    }

    fun resetItemAssignmentStatus() {
        _itemAssignmentStatus.value = CompetitionItemAssignmentStatus.Idle
    }

    fun addCompetition(name: String) {
        viewModelScope.launch {
            _competitionAddStatus.value = CompetitionAddStatus.Pending
            try {
                val response =
                    api.addCompetition(CompetitionAddRequest(data = CompetitionAddRequest.Data(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _competitionAddStatus.value = CompetitionAddStatus.Success
                    } else {
                        _competitionAddStatus.value = CompetitionAddStatus.Error(body.message)
                    }
                } else {
                    _competitionAddStatus.value =
                        CompetitionAddStatus.Error(context.getString(R.string.error_add_competitions))
                }
            } catch (_: Exception) {
                _competitionAddStatus.value =
                    CompetitionAddStatus.Error(context.getString(R.string.error_add_competitions))
            }
        }
    }

    fun assignItem(competitionId: Int, itemId: Int, assignment: Boolean) {
        viewModelScope.launch {
            _itemAssignmentStatus.value = CompetitionItemAssignmentStatus.Pending
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
                        _itemAssignmentStatus.value = CompetitionItemAssignmentStatus.Success
                    } else {
                        _itemAssignmentStatus.value =
                            CompetitionItemAssignmentStatus.Error(body.message)
                    }
                } else {
                    _itemAssignmentStatus.value =
                        CompetitionItemAssignmentStatus.Error(context.getString(R.string.error_item_assignment))
                }
            } catch (_: Exception) {
                _itemAssignmentStatus.value =
                    CompetitionItemAssignmentStatus.Error(context.getString(R.string.error_item_assignment))
            }
        }
    }
}
