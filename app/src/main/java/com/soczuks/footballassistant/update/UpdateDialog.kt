package com.soczuks.footballassistant.update

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.soczuks.footballassistant.R

@Composable
fun UpdateDialog(
    state: UpdateUiState,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onDismiss: () -> Unit,
    onCheckAgain: () -> Unit
) {
    when (state) {
        UpdateUiState.Idle, UpdateUiState.Checking -> Unit

        UpdateUiState.UpToDate -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.update_up_to_date_title)) },
            text = { Text(stringResource(R.string.update_up_to_date_message)) },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) }
            }
        )

        is UpdateUiState.Available -> ReleaseDialog(
            release = state.release,
            required = state.required,
            confirmText = stringResource(R.string.update_download),
            onConfirm = onDownload,
            onDismiss = onDismiss
        )

        is UpdateUiState.Downloading -> ProgressDialog(
            title = stringResource(R.string.update_downloading),
            progress = state.progress
        )

        is UpdateUiState.Verifying -> ProgressDialog(
            title = stringResource(R.string.update_verifying),
            progress = null
        )

        is UpdateUiState.ReadyToInstall -> ReleaseDialog(
            release = state.release,
            required = state.required,
            confirmText = stringResource(R.string.update_install),
            onConfirm = onInstall,
            onDismiss = onDismiss
        )

        is UpdateUiState.Error -> ErrorDialog(
            state = state,
            onRetry = {
                if (state.release == null) {
                    onCheckAgain()
                } else if (state.kind == UpdateError.INSTALLATION) {
                    onInstall()
                } else {
                    onDownload()
                }
            },
            onDismiss = onDismiss
        )
    }
}
