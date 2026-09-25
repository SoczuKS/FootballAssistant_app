package com.soczuks.footballassistant.ui.item.additemscreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.ui.item.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(onNavigateBack: () -> Unit, onItemCreated: () -> Unit, viewModel: ItemViewModel = hiltViewModel()) {


}
