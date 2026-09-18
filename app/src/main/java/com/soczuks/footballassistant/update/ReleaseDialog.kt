package com.soczuks.footballassistant.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.model.AppRelease

@Composable
internal fun ReleaseDialog(
    release: AppRelease,
    required: Boolean,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!required) {
                onDismiss()
            }
        },
        title = {
            Text(
                stringResource(
                    if (required) R.string.update_required_title
                    else R.string.update_available_title
                )
            )
        },
        text = {
            Column {
                Text(stringResource(R.string.update_version, release.versionName))
                Text(
                    stringResource(
                        R.string.update_size_mb,
                        release.sizeBytes.toDouble() / (1024.0 * 1024.0)
                    )
                )
                if (release.releaseNotes.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    release.releaseNotes.forEach { Text("• $it") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmText) }
        },
        dismissButton = if (required) {
            null
        } else {
            { TextButton(onClick = onDismiss) { Text(stringResource(R.string.update_later)) } }
        }
    )
}
