package com.soczuks.footballassistant.ui.competition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.CompetitionAddRequest
import com.soczuks.footballassistant.api.model.request.CompetitionAddRequestData
import com.soczuks.footballassistant.ui.competition.addcompetitionscreen.CreateCompetitionState
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
) :
    ViewModel() {
    private val _uiState = MutableStateFlow<CompetitionListUiState>(CompetitionListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _createCompetitionState =
        MutableStateFlow<CreateCompetitionState>(CreateCompetitionState.Idle)
    val createCompetitionState = _createCompetitionState.asStateFlow()

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
        _createCompetitionState.value = CreateCompetitionState.Idle
    }

    fun addCompetition(name: String) {
        viewModelScope.launch {
            _createCompetitionState.value = CreateCompetitionState.Loading
            try {
                val response = api.addCompetition(CompetitionAddRequest(data = CompetitionAddRequestData(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _createCompetitionState.value = CreateCompetitionState.Success
                    } else {
                        _createCompetitionState.value = CreateCompetitionState.Error(body.message)
                    }
                } else {
                    _createCompetitionState.value = CreateCompetitionState.Error(context.getString(R.string.error_add_competitions))
                }
            } catch(_: Exception) {
                _createCompetitionState.value = CreateCompetitionState.Error(context.getString(R.string.error_add_competitions))
            }
        }
    }
}