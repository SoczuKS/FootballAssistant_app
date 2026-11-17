package com.soczuks.footballassistant.ui.match.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.soczuks.footballassistant.api.model.Match

@Composable
internal fun PastMatchScreen(match: Match) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Past")
    }
}
