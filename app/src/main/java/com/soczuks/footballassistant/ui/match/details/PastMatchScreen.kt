package com.soczuks.footballassistant.ui.match.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soczuks.footballassistant.api.model.Match
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.match.MatchViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun PastMatchScreen(
    match: Match, matchViewModel: MatchViewModel, updateStatus: ApiActionStatus
) {
    val resultProvided = match.homeScore != null && match.awayScore != null
    val isPending = updateStatus is ApiActionStatus.Pending

    var homeScore by rememberSaveable(match.id) {
        mutableStateOf(match.homeScore?.toString().orEmpty())
    }
    var awayScore by rememberSaveable(match.id) {
        mutableStateOf(match.awayScore?.toString().orEmpty())
    }

    val matchDate = match.matchDate.atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = matchDate,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = match.homeTeam.name,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (resultProvided) {
                Text(
                    text = "${match.homeScore}:${match.awayScore}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                OutlinedTextField(
                    value = homeScore,
                    onValueChange = { homeScore = it.filter(Char::isDigit) },
                    modifier = Modifier.width(72.dp),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )

                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = awayScore,
                    onValueChange = { awayScore = it.filter(Char::isDigit) },
                    modifier = Modifier.width(72.dp),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )
            }

            Text(
                text = match.awayTeam.name,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (!resultProvided) {
            Spacer(modifier = Modifier.height(32.dp))

            val homeScoreValue = homeScore.toIntOrNull()
            val awayScoreValue = awayScore.toIntOrNull()
            val scoresValid = homeScoreValue != null && awayScoreValue != null

            Button(
                onClick = {
                    if (scoresValid) {
                        matchViewModel.updateMatch(
                            matchId = match.id,
                            homeScore = homeScoreValue,
                            awayScore = awayScoreValue
                        )
                    }
                }, enabled = scoresValid && !isPending, modifier = Modifier.fillMaxWidth()
            ) {
                if (isPending) {
                    CircularProgressIndicator(
                        modifier = Modifier, strokeWidth = 2.dp
                    )
                } else {
                    Text("Save result")
                }
            }
        }
    }
}
