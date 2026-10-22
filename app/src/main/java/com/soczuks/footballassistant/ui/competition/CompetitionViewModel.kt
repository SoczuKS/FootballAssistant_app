package com.soczuks.footballassistant.ui.competition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.AddCompetitionRequest
import com.soczuks.footballassistant.api.model.request.AddCompetitionRequestData
import com.soczuks.footballassistant.ui.competition.addcompetitionscreen.AddCompetitionState
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

    fun addCompetition(name: String) {
        viewModelScope.launch {
            _addCompetitionState.value = AddCompetitionState.Loading
            try {
                val response =
                    api.addCompetition(AddCompetitionRequest(data = AddCompetitionRequestData(name)))

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
}