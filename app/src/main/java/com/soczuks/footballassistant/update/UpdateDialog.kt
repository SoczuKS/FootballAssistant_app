package com.soczuks.footballassistant.update

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.soczuks.footballassistant.R

@Composable
fun UpdateDialog(
    state: UpdateStatus,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onDismiss: () -> Unit,
    onCheckAgain: () -> Unit
) {
    when (state) {
        UpdateStatus.Idle, UpdateStatus.Checking -> Unit

        UpdateStatus.UpToDate -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.update_up_to_date_title)) },
            text = { Text(stringResource(R.string.update_up_to_date_message)) },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) }
            })

        is UpdateStatus.Available -> ReleaseDialog(
            release = state.release,
            required = state.required,
            confirmText = stringResource(R.string.update_download),
            onConfirm = onDownload,
            onDismiss = onDismiss
        )

        is UpdateStatus.Downloading -> ProgressDialog(
            title = stringResource(R.string.update_downloading), progress = state.progress
        )

        is UpdateStatus.Verifying -> ProgressDialog(
            title = stringResource(R.string.update_verifying), progress = null
        )

        is UpdateStatus.ReadyToInstall -> ReleaseDialog(
            release = state.release,
            required = state.required,
            confirmText = stringResource(R.string.update_install),
            onConfirm = onInstall,
            onDismiss = onDismiss
        )

        is UpdateStatus.Error -> ErrorDialog(
            state = state, onRetry = {
                if (state.release == null) {
                    onCheckAgain()
                } else if (state.kind == UpdateError.INSTALLATION) {
                    onInstall()
                } else {
                    onDownload()
                }
            }, onDismiss = onDismiss
        )
    }
}
