package com.soczuks.footballassistant.ui.item

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.ItemAddRequest
import com.soczuks.footballassistant.api.model.request.ItemAddRequestData
import com.soczuks.footballassistant.ui.item.additemscreen.CreateItemState
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

    private val _createItemState = MutableStateFlow<CreateItemState>(CreateItemState.Idle)
    val createItemState = _createItemState.asStateFlow()


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
        _createItemState.value = CreateItemState.Idle
    }

    fun addItem(name: String) {
        viewModelScope.launch {
            _createItemState.value = CreateItemState.Loading
            try {
                val response = api.addItem(ItemAddRequest(data = ItemAddRequestData(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _createItemState.value = CreateItemState.Success
                    } else {
                        _createItemState.value = CreateItemState.Error(body.message)
                    }
                } else {
                    _createItemState.value =
                        CreateItemState.Error(context.getString(R.string.error_add_item))
                }
            } catch (_: Exception) {
                _createItemState.value =
                    CreateItemState.Error(context.getString(R.string.error_add_item))
            }
        }
    }
}