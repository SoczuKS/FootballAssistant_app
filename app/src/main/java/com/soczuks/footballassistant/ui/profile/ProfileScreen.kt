package com.soczuks.footballassistant.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun ProfileScreen(onNavigateBack: () -> Unit, userProfileViewModel: UserProfileViewModel = hiltViewModel()) {
    val userProfileLoadStatus by userProfileViewModel.loadStatus.collectAsState()

    Scaffold(topBar = {
        TopBar(title = stringResource(R.string.user_profile_title), onNavigateBack = onNavigateBack)
    }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val status = userProfileLoadStatus) {
                UserProfileLoadStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is UserProfileLoadStatus.Error -> ErrorContent(
                    message = status.message, onRetry = userProfileViewModel::load
                )

                is UserProfileLoadStatus.Success -> {

                }
            }
        }
    }
}
