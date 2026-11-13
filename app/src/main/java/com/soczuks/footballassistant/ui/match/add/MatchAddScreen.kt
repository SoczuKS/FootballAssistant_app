package com.soczuks.footballassistant.ui.match.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.api.model.Competition
import com.soczuks.footballassistant.api.model.Team
import com.soczuks.footballassistant.ui.common.ApiActionStatus
import com.soczuks.footballassistant.ui.common.ApiListStatus
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel
import com.soczuks.footballassistant.ui.match.MatchViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchAddScreen(
    onNavigateBack: () -> Unit,
    onMatchCreated: () -> Unit,
    matchViewModel: MatchViewModel = hiltViewModel(),
    competitionViewModel: CompetitionViewModel = hiltViewModel()
) {
    var competitionId by rememberSaveable { mutableIntStateOf(0) }
    var homeTeamId by rememberSaveable { mutableIntStateOf(0) }
    var awayTeamId by rememberSaveable { mutableIntStateOf(0) }
    var matchDate by rememberSaveable { mutableStateOf<Instant?>(null) }

    var competitionMenuExpanded by remember { mutableStateOf(false) }
    var homeTeamMenuExpanded by remember { mutableStateOf(false) }
    var awayTeamMenuExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val matchAddStatus by matchViewModel.matchAddStatus.collectAsState()
    val competitionListStatus by competitionViewModel.listStatus.collectAsState()
    val competitions = (competitionListStatus as? ApiListStatus.Success)?.data.orEmpty()
    val selectedCompetition = competitions.firstOrNull { it.id == competitionId }
    val teams = selectedCompetition?.teams.orEmpty()

    val selectedHomeTeam = teams.firstOrNull { it.id == homeTeamId }
    val selectedAwayTeam = teams.firstOrNull { it.id == awayTeamId }

    LaunchedEffect(matchAddStatus) {
        if (matchAddStatus is ApiActionStatus.Success) {
            onMatchCreated()
            onNavigateBack()
            matchViewModel.resetAddStatus()
        }
    }

    LaunchedEffect(competitionId) {
        homeTeamId = 0
        awayTeamId = 0
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.match_add_title)) }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_description)
                )
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompetitionSelector(
                competitions = competitions,
                selectedCompetition = selectedCompetition,
                expanded = competitionMenuExpanded,
                onExpandedChange = { competitionMenuExpanded = it },
                onSelected = {
                    competitionId = it.id
                    competitionMenuExpanded = false
                })

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TeamSelector(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.match_add_home_team_label),
                    teams = teams,
                    selectedTeam = selectedHomeTeam,
                    expanded = homeTeamMenuExpanded,
                    enabled = selectedCompetition != null,
                    onExpandedChange = { homeTeamMenuExpanded = it },
                    onSelected = {
                        homeTeamId = it.id
                        homeTeamMenuExpanded = false
                    })

                TeamSelector(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.match_add_away_team_label),
                    teams = teams,
                    selectedTeam = selectedAwayTeam,
                    expanded = awayTeamMenuExpanded,
                    enabled = selectedCompetition != null,
                    onExpandedChange = { awayTeamMenuExpanded = it },
                    onSelected = {
                        awayTeamId = it.id
                        awayTeamMenuExpanded = false
                    })
            }

            OutlinedButton(
                onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(matchDate?.let {
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())
                        .format(it)
                } ?: stringResource(R.string.match_date_label))
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    matchViewModel.addMatch(competitionId, homeTeamId, awayTeamId, matchDate!!)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = competitionId != 0 && homeTeamId != 0 && awayTeamId != 0 && matchDate != null && matchAddStatus !is ApiActionStatus.Pending
            ) {
                if (matchAddStatus is ApiActionStatus.Pending) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.match_add_add_button))
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    val currentTime = matchDate?.atZone(ZoneId.systemDefault())?.toLocalTime()
                    matchDate =
                        date.atTime(currentTime ?: LocalTime.NOON).atZone(ZoneId.systemDefault())
                            .toInstant()

                    showDatePicker = false
                    showTimePicker = true
                }
            }) { Text(stringResource(R.string.ok)) }
        }, dismissButton = {
            TextButton(onClick = {
                showDatePicker = false
            }) { Text(stringResource(R.string.cancel)) }
        }) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val currentTime = matchDate?.atZone(ZoneId.systemDefault())?.toLocalTime()

        val timePickerState = remember {
            TimePickerState(
                initialHour = currentTime?.hour ?: 12,
                initialMinute = currentTime?.minute ?: 0,
                is24Hour = true
            )
        }

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val date =
                        matchDate?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: LocalDate.now()
                    matchDate = date.atTime(timePickerState.hour, timePickerState.minute)
                        .atZone(ZoneId.systemDefault()).toInstant()

                    showTimePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTimePicker = false
                }) { Text(stringResource(R.string.cancel)) }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompetitionSelector(
    competitions: List<Competition>,
    selectedCompetition: Competition?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelected: (Competition) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = onExpandedChange, modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCompetition?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.match_add_competition_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            competitions.forEach { competition ->
                DropdownMenuItem(
                    text = { Text(competition.name) },
                    onClick = { onSelected(competition) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSelector(
    modifier: Modifier,
    label: String,
    teams: List<Team>,
    selectedTeam: Team?,
    expanded: Boolean,
    enabled: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelected: (Team) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        if (enabled) {
            onExpandedChange(it)
        }
    }, modifier = modifier) {
        OutlinedTextField(
            value = selectedTeam?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable
                )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            teams.forEach { team ->
                DropdownMenuItem(text = { Text(team.name) }, onClick = { onSelected(team) })
            }
        }
    }
}
