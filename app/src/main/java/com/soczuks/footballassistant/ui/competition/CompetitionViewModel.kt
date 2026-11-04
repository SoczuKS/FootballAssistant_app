package com.soczuks.footballassistant.ui.competition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.competition.CompetitionAddRequest
import com.soczuks.footballassistant.api.model.request.competition.CompetitionAddRequestData
import com.soczuks.footballassistant.api.model.request.competition.CompetitionItemAssignmentRequest
import com.soczuks.footballassistant.api.model.request.competition.CompetitionItemAssignmentRequestData
import com.soczuks.footballassistant.ui.competition.addcompetitionscreen.AddCompetitionState
import com.soczuks.footballassistant.ui.competition.competitiondetailsscreen.CompetitionItemAssignmentState
import com.soczuks.footballassistant.ui.competition.competitionlistscreen.CompetitionListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompetitionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<CompetitionListUiState>(CompetitionListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _addCompetitionState =
        MutableStateFlow<AddCompetitionState>(AddCompetitionState.Idle)
    val addCompetitionState = _addCompetitionState.asStateFlow()

    private val _assignmentState =
        MutableStateFlow<CompetitionItemAssignmentState>(CompetitionItemAssignmentState.Idle)
    val assignmentState = _assignmentState.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _uiState.value = CompetitionListUiState.Loading
            }

            try {
                val response = api.getCompetitions()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _uiState.value =
                            CompetitionListUiState.Success(response.body()!!.data.competitions)
                    } else {
                        _uiState.value =
                            CompetitionListUiState.Error(response.body()!!.message)
                    }
                } else {
                    _uiState.value =
                        CompetitionListUiState.Error(context.getString(R.string.error_load_competitions))
                }
            } catch (_: Exception) {
                _uiState.value =
                    CompetitionListUiState.Error(context.getString(R.string.error_load_competitions))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetCreateState() {
        _addCompetitionState.value = AddCompetitionState.Idle
    }

    fun resetAssignmentState() {
        _assignmentState.value = CompetitionItemAssignmentState.Idle
    }

    fun addCompetition(name: String) {
        viewModelScope.launch {
            _addCompetitionState.value = AddCompetitionState.Loading
            try {
                val response =
                    api.addCompetition(CompetitionAddRequest(data = CompetitionAddRequestData(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _addCompetitionState.value = AddCompetitionState.Success
                    } else {
                        _addCompetitionState.value = AddCompetitionState.Error(body.message)
                    }
                } else {
                    _addCompetitionState.value =
                        AddCompetitionState.Error(context.getString(R.string.error_add_competitions))
                }
            } catch (_: Exception) {
                _addCompetitionState.value =
                    AddCompetitionState.Error(context.getString(R.string.error_add_competitions))
            }
        }
    }

    fun assignItem(competitionId: Int, itemId: Int, assignment: Boolean) {
        viewModelScope.launch {
            _assignmentState.value = CompetitionItemAssignmentState.Pending
            try {
                val response = api.assignItemToCompetition(
                    CompetitionItemAssignmentRequest(
                        data = CompetitionItemAssignmentRequestData(
                            competitionId,
                            itemId,
                            assignment
                        )
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _assignmentState.value = CompetitionItemAssignmentState.Success
                    } else {
                        _assignmentState.value = CompetitionItemAssignmentState.Error(body.message)
                    }
                } else {
                    _assignmentState.value =
                        CompetitionItemAssignmentState.Error(context.getString(R.string.error_item_assignment))
                }
            } catch (_: Exception) {
                _assignmentState.value =
                    CompetitionItemAssignmentState.Error(context.getString(R.string.error_item_assignment))
            }
        }
    }
}