package com.soczuks.footballassistant.ui.item

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.AddItemRequest
import com.soczuks.footballassistant.api.model.request.AddItemRequestData
import com.soczuks.footballassistant.ui.item.additemscreen.AddItemState
import com.soczuks.footballassistant.ui.item.itemlistscreen.ItemListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FootballAssistantApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<ItemListUiState>(ItemListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _addItemState = MutableStateFlow<AddItemState>(AddItemState.Idle)
    val addItemState = _addItemState.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _uiState.value = ItemListUiState.Loading
            }

            try {
                val response = api.getItems()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _uiState.value =
                            ItemListUiState.Success(response.body()!!.data.items)
                    } else {
                        _uiState.value = ItemListUiState.Error(response.body()!!.message)
                    }
                } else {
                    _uiState.value =
                        ItemListUiState.Error(context.getString(R.string.error_load_items))
                }
            } catch (_: Exception) {
                _uiState.value = ItemListUiState.Error(context.getString(R.string.error_load_items))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetCreateState() {
        _addItemState.value = AddItemState.Idle
    }

    fun addItem(name: String) {
        viewModelScope.launch {
            _addItemState.value = AddItemState.Loading
            try {
                val response = api.addItem(AddItemRequest(data = AddItemRequestData(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _addItemState.value = AddItemState.Success
                    } else {
                        _addItemState.value = AddItemState.Error(body.message)
                    }
                } else {
                    _addItemState.value =
                        AddItemState.Error(context.getString(R.string.error_add_item))
                }
            } catch (_: Exception) {
                _addItemState.value =
                    AddItemState.Error(context.getString(R.string.error_add_item))
            }
        }
    }
}