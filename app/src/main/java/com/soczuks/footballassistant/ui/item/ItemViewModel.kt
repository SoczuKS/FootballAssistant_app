package com.soczuks.footballassistant.ui.item

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.FootballAssistantApi
import com.soczuks.footballassistant.api.model.request.item.ItemAddRequest
import com.soczuks.footballassistant.ui.item.add.ItemAddStatus
import com.soczuks.footballassistant.ui.item.list.ItemDeleteStatus
import com.soczuks.footballassistant.ui.item.list.ItemListStatus
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
    private val _listStatus = MutableStateFlow<ItemListStatus>(ItemListStatus.Loading)
    val listStatus = _listStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _itemAddStatus = MutableStateFlow<ItemAddStatus>(ItemAddStatus.Idle)
    val itemAddStatus = _itemAddStatus.asStateFlow()

    private val _itemDeleteStatus = MutableStateFlow<ItemDeleteStatus>(ItemDeleteStatus.Idle)
    val itemDeleteStatus = _itemDeleteStatus.asStateFlow()

    init {
        load()
    }

    fun load(fromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _listStatus.value = ItemListStatus.Loading
            }

            try {
                val response = api.getItems()
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.statusCode == 0) {
                        _listStatus.value = ItemListStatus.Success(response.body()!!.data.items)
                    } else {
                        _listStatus.value = ItemListStatus.Error(response.body()!!.message)
                    }
                } else {
                    _listStatus.value =
                        ItemListStatus.Error(context.getString(R.string.error_load_items))
                }
            } catch (_: Exception) {
                _listStatus.value =
                    ItemListStatus.Error(context.getString(R.string.error_load_items))
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun resetCreateState() {
        _itemAddStatus.value = ItemAddStatus.Idle
    }

    fun addItem(name: String) {
        viewModelScope.launch {
            _itemAddStatus.value = ItemAddStatus.Pending
            try {
                val response = api.addItem(ItemAddRequest(data = ItemAddRequest.Data(name)))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _itemAddStatus.value = ItemAddStatus.Success
                    } else {
                        _itemAddStatus.value = ItemAddStatus.Error(body.message)
                    }
                } else {
                    _itemAddStatus.value =
                        ItemAddStatus.Error(context.getString(R.string.error_add_item))
                }
            } catch (_: Exception) {
                _itemAddStatus.value =
                    ItemAddStatus.Error(context.getString(R.string.error_add_item))
            }
        }
    }

    fun resetDeleteStatus() {
        _itemDeleteStatus.value = ItemDeleteStatus.Idle
    }

    fun deleteItem(itemId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _itemDeleteStatus.value = ItemDeleteStatus.Pending
            try {
                val response = api.deleteItem(itemId)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    if (body.statusCode == 0) {
                        _itemDeleteStatus.value = ItemDeleteStatus.Success
                        onSuccess()
                        load()
                    } else {
                        _itemDeleteStatus.value = ItemDeleteStatus.Error(body.message)
                    }
                } else {
                    _itemDeleteStatus.value =
                        ItemDeleteStatus.Error(context.getString(R.string.item_delete_error))
                }
            } catch (_: Exception) {
                _itemDeleteStatus.value =
                    ItemDeleteStatus.Error(context.getString(R.string.item_delete_error))
            }
        }
    }
}
