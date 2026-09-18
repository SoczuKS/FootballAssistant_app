package com.soczuks.footballassistant.update

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.soczuks.footballassistant.R

@Composable
internal fun ErrorDialog(
    state: UpdateUiState.Error,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val message = when (state.kind) {
        UpdateError.NETWORK -> R.string.update_error_network
        UpdateError.DOWNLOAD -> R.string.update_error_download
        UpdateError.VERIFICATION -> R.string.update_error_verification
        UpdateError.INSTALLATION -> R.string.update_error_installation
    }
    AlertDialog(
        onDismissRequest = { if (!state.required) onDismiss() },
        title = { Text(stringResource(R.string.update_error_title)) },
        text = { Text(stringResource(message)) },
        confirmButton = {
            TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
        },
        dismissButton = if (state.required) null else {
            { TextButton(onClick = onDismiss) { Text(stringResource(R.string.update_later)) } }
        }
    )
}
