package com.soczuks.footballassistant.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ErrorContent
import com.soczuks.footballassistant.ui.navigation.TopBar

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: (showLoginRequiredMessage: Boolean) -> Unit,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfileLoadStatus by userProfileViewModel.loadStatus.collectAsState()
    val changeEmailStatus by userProfileViewModel.changeEmailStatus.collectAsState()
    val changeUserDataStatus by userProfileViewModel.changeUserDataStatus.collectAsState()
    val changePasswordStatus by userProfileViewModel.changePasswordStatus.collectAsState()
    val logoutStatus by userProfileViewModel.logoutStatus.collectAsState()
    val isProfileRefreshing by userProfileViewModel.isRefreshing.collectAsState()
    var showLoginRequiredMessage by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(changeEmailStatus) {
        if (changeEmailStatus is ApiActionStatus.Success) {
            showLoginRequiredMessage = true
            userProfileViewModel.logout()
        }
    }
    LaunchedEffect(changePasswordStatus) {
        if (changePasswordStatus is ApiActionStatus.Success) {
            showLoginRequiredMessage = true
            userProfileViewModel.logout()
        }
    }
    LaunchedEffect(changeUserDataStatus) {
        if (changeUserDataStatus is ApiActionStatus.Success) {
            userProfileViewModel.load(refresh = true)
        }
    }
    LaunchedEffect(logoutStatus) {
        if (logoutStatus is ApiActionStatus.Success) {
            onLogout(showLoginRequiredMessage)
        }
    }

    Scaffold(
        topBar = {
            TopBar(title = stringResource(R.string.user_profile_title), onNavigateBack = onNavigateBack)
        },
        bottomBar = {
            Button(
                onClick = {
                    showLoginRequiredMessage = false
                    userProfileViewModel.logout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                enabled = logoutStatus !is ApiActionStatus.Pending
            ) {
                if (logoutStatus is ApiActionStatus.Pending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.logout_button))
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val status = userProfileLoadStatus) {
                UserProfileLoadStatus.Loading -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center)
                )

                is UserProfileLoadStatus.Error -> ErrorContent(
                    message = status.message,
                    onRetry = userProfileViewModel::load
                )

                is UserProfileLoadStatus.Success -> {
                    var firstname by rememberSaveable(status.data.id) {
                        mutableStateOf(status.data.firstname)
                    }
                    var lastname by rememberSaveable(status.data.id) {
                        mutableStateOf(status.data.lastname)
                    }
                    var email by rememberSaveable(status.data.id) {
                        mutableStateOf(status.data.email)
                    }
                    var confirmEmail by rememberSaveable(status.data.id) {
                        mutableStateOf("")
                    }
                    val currentPassword = rememberTextFieldState()
                    val password = rememberTextFieldState()
                    val confirmPassword = rememberTextFieldState()
                    var passwordMismatch by rememberSaveable { mutableStateOf(false) }

                    LaunchedEffect(
                        currentPassword.text,
                        password.text,
                        confirmPassword.text
                    ) {
                        passwordMismatch = false
                    }
                    LaunchedEffect(changePasswordStatus) {
                        if (changePasswordStatus is ApiActionStatus.Success) {
                            currentPassword.edit { delete(0, length) }
                            password.edit { delete(0, length) }
                            confirmPassword.edit { delete(0, length) }
                        }
                    }

                    Column(Modifier.fillMaxSize()) {
                        if (isProfileRefreshing) {
                            LinearProgressIndicator(Modifier.fillMaxWidth())
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = stringResource(R.string.profile_user_info_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                OutlinedTextField(
                                    value = firstname,
                                    onValueChange = {
                                        firstname = it
                                        userProfileViewModel.resetChangeUserDataStatus()
                                    },
                                    label = { Text(stringResource(R.string.firstname_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    enabled = changeUserDataStatus !is ApiActionStatus.Pending
                                )
                                OutlinedTextField(
                                    value = lastname,
                                    onValueChange = {
                                        lastname = it
                                        userProfileViewModel.resetChangeUserDataStatus()
                                    },
                                    label = { Text(stringResource(R.string.lastname_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    enabled = changeUserDataStatus !is ApiActionStatus.Pending
                                )
                                Button(
                                    onClick = {
                                        userProfileViewModel.changeUserData(
                                            firstname.trim(),
                                            lastname.trim()
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = (firstname.trim() != status.data.firstname ||
                                        lastname.trim() != status.data.lastname) &&
                                        firstname.isNotBlank() && lastname.isNotBlank() &&
                                        changeUserDataStatus !is ApiActionStatus.Pending
                                ) {
                                    if (changeUserDataStatus is ApiActionStatus.Pending) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(stringResource(R.string.profile_update_user_info))
                                    }
                                }
                                ActionFeedback(changeUserDataStatus)
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = stringResource(R.string.profile_password_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                OutlinedSecureTextField(
                                    state = currentPassword,
                                    label = { Text(stringResource(R.string.current_password_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Next
                                    ),
                                    enabled = changePasswordStatus !is ApiActionStatus.Pending
                                )
                                OutlinedSecureTextField(
                                    state = password,
                                    label = { Text(stringResource(R.string.password_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Next
                                    ),
                                    enabled = changePasswordStatus !is ApiActionStatus.Pending
                                )
                                OutlinedSecureTextField(
                                    state = confirmPassword,
                                    label = { Text(stringResource(R.string.confirm_password_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    isError = passwordMismatch,
                                    supportingText = if (passwordMismatch) {
                                        { Text(stringResource(R.string.passwords_do_not_match)) }
                                    } else null,
                                    enabled = changePasswordStatus !is ApiActionStatus.Pending
                                )
                                Button(
                                    onClick = {
                                        val newPassword = password.text.toString()
                                        if (newPassword == confirmPassword.text.toString()) {
                                            passwordMismatch = false
                                            userProfileViewModel.changePassword(
                                                currentPassword.text.toString(),
                                                newPassword
                                            )
                                        } else {
                                            passwordMismatch = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = currentPassword.text.isNotBlank() &&
                                        password.text.isNotBlank() &&
                                        confirmPassword.text.isNotBlank() &&
                                        changePasswordStatus !is ApiActionStatus.Pending
                                ) {
                                    if (changePasswordStatus is ApiActionStatus.Pending) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(stringResource(R.string.profile_change_password))
                                    }
                                }
                                ActionFeedback(changePasswordStatus)
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = stringResource(R.string.profile_email_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        userProfileViewModel.resetChangeEmailStatus()
                                    },
                                    label = { Text(stringResource(R.string.email_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email
                                    ),
                                    enabled = changeEmailStatus !is ApiActionStatus.Pending
                                )
                                val emailsMatch = email.trim() == confirmEmail.trim()
                                OutlinedTextField(
                                    value = confirmEmail,
                                    onValueChange = {
                                        confirmEmail = it
                                        userProfileViewModel.resetChangeEmailStatus()
                                    },
                                    label = {
                                        Text(stringResource(R.string.confirm_email_label))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email
                                    ),
                                    isError = confirmEmail.isNotBlank() && !emailsMatch,
                                    supportingText = if (confirmEmail.isNotBlank() && !emailsMatch) {
                                        { Text(stringResource(R.string.emails_do_not_match)) }
                                    } else null,
                                    enabled = changeEmailStatus !is ApiActionStatus.Pending
                                )
                                Button(
                                    onClick = {
                                        userProfileViewModel.changeEmail(email.trim())
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = email.trim() != status.data.email &&
                                        email.isNotBlank() &&
                                        confirmEmail.isNotBlank() && emailsMatch &&
                                        changeEmailStatus !is ApiActionStatus.Pending
                                ) {
                                    if (changeEmailStatus is ApiActionStatus.Pending) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(stringResource(R.string.profile_change_email))
                                    }
                                }
                                ActionFeedback(changeEmailStatus)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionFeedback(status: ApiActionStatus) {
    when (status) {
        is ApiActionStatus.Error -> Text(
            text = status.message,
            color = MaterialTheme.colorScheme.error
        )

        ApiActionStatus.Success -> Text(
            text = stringResource(R.string.profile_update_success),
            color = MaterialTheme.colorScheme.primary
        )

        ApiActionStatus.Idle, ApiActionStatus.Pending -> Unit
    }
}
