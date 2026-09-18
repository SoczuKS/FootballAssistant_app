package com.soczuks.footballassistant.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.soczuks.footballassistant.R

@Composable
internal fun ProgressDialog(title: String, progress: Int?) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = {
            if (progress == null) {
                CircularProgressIndicator()
            } else {
                Column {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.update_progress, progress))
                }
            }
        },
        confirmButton = {}
    )
}
