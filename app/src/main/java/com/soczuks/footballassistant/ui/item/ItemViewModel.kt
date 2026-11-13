package com.soczuks.footballassistant.ui.item

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.Item
import com.soczuks.footballassistant.api.model.request.item.ItemAddRequest
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    @ApplicationContext private val context: Context, private val api: FootballAssistantApi
) : ViewModel() {
    private val _listStatus = MutableStateFlow<ApiListStatus<Item>>(ApiListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _itemAddStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val itemAddStatus = _itemAddStatus.asStateFlow()

    private val _itemDeleteStatus = MutableStateFlow<ApiActionStatus>(ApiActionStatus.Idle)
    val itemDeleteStatus = _itemDeleteStatus.asStateFlow()

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
                val response = api.getItems()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _listStatus.value = ApiListStatus.Success(response.body()!!.data.items)
                    } else {
                        _listStatus.value = ApiListStatus.Error(response.body()!!.message)
                    }
                } else {
                    _listStatus.value =
                        ApiListStatus.Error(context.getString(R.string.item_load_error))
                }
            } catch (_: Exception) {
                _listStatus.value =
                    ApiListStatus.Error(context.getString(R.string.item_load_error))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetAddStatus() {
        _itemAddStatus.value = ApiActionStatus.Idle
    }

    fun addItem(name: String) {
        viewModelScope.launch {
            _itemAddStatus.value = ApiActionStatus.Pending
            try {
                val response = api.addItem(ItemAddRequest(data = ItemAddRequest.Data(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _itemAddStatus.value = ApiActionStatus.Success
                    } else {
                        _itemAddStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _itemAddStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.item_add_error))
                }
            } catch (_: Exception) {
                _itemAddStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.item_add_error))
            }
        }
    }

    fun resetDeleteStatus() {
        _itemDeleteStatus.value = ApiActionStatus.Idle
    }

    fun deleteItem(itemId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _itemDeleteStatus.value = ApiActionStatus.Pending
            try {
                val response = api.deleteItem(itemId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _itemDeleteStatus.value = ApiActionStatus.Success
                        onSuccess()
                        load()
                    } else {
                        _itemDeleteStatus.value = ApiActionStatus.Error(body.message)
                    }
                } else {
                    _itemDeleteStatus.value =
                        ApiActionStatus.Error(context.getString(R.string.item_delete_error))
                }
            } catch (_: Exception) {
                _itemDeleteStatus.value =
                    ApiActionStatus.Error(context.getString(R.string.item_delete_error))
            }
        }
    }
}
