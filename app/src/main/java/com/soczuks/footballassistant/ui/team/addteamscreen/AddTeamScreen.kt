package com.soczuks.footballassistant.ui.team.addteamscreen

import android.graphics.Color.HSVToColor
import android.graphics.Color.colorToHSV
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel
import com.soczuks.footballassistant.ui.competition.competitionlistscreen.CompetitionListUiState
import com.soczuks.footballassistant.ui.team.TeamViewModel
import com.soczuks.footballassistant.ui.team.addteamscreen.AddTeamState.Loading
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeamScreen(
    onNavigateBack: () -> Unit,
    onTeamCreated: () -> Unit,
    viewModel: TeamViewModel = hiltViewModel(),
    competitionViewModel: CompetitionViewModel = hiltViewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var competitionId by rememberSaveable { mutableStateOf<Int?>(null) }

    var showColorPicker by rememberSaveable { mutableStateOf(false) }
    var showCompetitionPicker by rememberSaveable { mutableStateOf(false) }

    val addTeamState by viewModel.addTeamState.collectAsState()
    val competitionState by competitionViewModel.uiState.collectAsState()

    LaunchedEffect(addTeamState) {
        if (addTeamState is AddTeamState.Success) {
            onTeamCreated()
            onNavigateBack()
            viewModel.resetAddState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_team_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.team_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.team_color_label),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            ColorPreview(color = color, onClick = { showColorPicker = true })
            Spacer(modifier = Modifier.height(32.dp))

            val selectedCompetition = when (val state = competitionState) {
                is CompetitionListUiState.Success -> state.competitions.find { it.id == competitionId }
                else -> null
            }

            ExposedDropdownMenuBox(
                expanded = showCompetitionPicker,
                onExpandedChange = {
                    if (competitionState is CompetitionListUiState.Success) {
                        showCompetitionPicker = !showCompetitionPicker
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                val fillMaxWidth = Modifier
                    .fillMaxWidth()
                OutlinedTextField(
                    value = selectedCompetition?.name.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.team_competition_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCompetitionPicker)
                    },
                    modifier = fillMaxWidth.menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        competitionState is CompetitionListUiState.Success
                    ),
                    enabled = competitionState is CompetitionListUiState.Success
                )

                ExposedDropdownMenu(
                    expanded = showCompetitionPicker,
                    onDismissRequest = { showCompetitionPicker = false }) {
                    val competitions =
                        (competitionState as? CompetitionListUiState.Success)?.competitions.orEmpty()

                    competitions.forEach { competition ->
                        DropdownMenuItem(text = { Text(competition.name) }, onClick = {
                            competitionId = competition.id
                            showCompetitionPicker = false
                        })
                    }
                }
            }
            when (val state = competitionState) {
                CompetitionListUiState.Loading -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }

                is CompetitionListUiState.Error -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is CompetitionListUiState.Success -> Unit
            }
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.team_address_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    competitionId?.let { competitionId ->
                        viewModel.addTeam(
                            name,
                            color,
                            competitionId,
                            address
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && color.isNotBlank() && competitionId != null && addTeamState !is Loading
            ) {
                if (addTeamState is Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.add_team_title))
                }
            }

            if (addTeamState is AddTeamState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (addTeamState as AddTeamState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (showColorPicker) {
            TeamColorPickerDialog(
                initialColor = color,
                onDismiss = {
                    showColorPicker = false
                },
                onColorSelected = { newColor ->
                    color = newColor
                    showColorPicker = false
                }
            )
        }
    }
}

@Composable
private fun TeamColorPickerDialog(
    initialColor: String,
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    val initialColor = remember(initialColor) {
        if (initialColor.isNotBlank()) {
            Color(initialColor.toColorInt())
        } else {
            Color.White
        }
    }

    val initialHsv = remember(initialColor) {
        FloatArray(3).also {
            colorToHSV(initialColor.toArgb(), it)
        }
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val selectedColor = remember(hue, saturation, value) {
        Color(HSVToColor(floatArrayOf(hue, saturation, value)))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.choose_team_color))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SaturationValuePicker(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onPositionChanged = { newSaturation, newValue ->
                        saturation = newSaturation
                        value = newValue
                    }
                )

                HuePicker(
                    hue = hue,
                    onHueChanged = { hue = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(selectedColor)
                )

                Text(
                    text = selectedColor.toHexString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(selectedColor.toHexString())
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun SaturationValuePicker(
    hue: Float,
    saturation: Float,
    value: Float,
    onPositionChanged: (Float, Float) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(hue) {
                detectDragGestures(
                    onDragStart = { position ->
                        updateSaturationValue(position, size.width, size.height) {
                            onPositionChanged(it.first, it.second)
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        updateSaturationValue(change.position, size.width, size.height) {
                            onPositionChanged(it.first, it.second)
                        }
                    }
                )
            }
    ) {
        val hueColor = Color(
            HSVToColor(floatArrayOf(hue, 1f, 1f))
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.White, hueColor)
            )
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black)
            )
        )

        drawCircle(
            color = Color.White,
            radius = 10.dp.toPx(),
            center = Offset(
                x = saturation * size.width,
                y = (1f - value) * size.height
            )
        )

        drawCircle(
            color = Color.Black,
            radius = 7.dp.toPx(),
            center = Offset(
                x = saturation * size.width,
                y = (1f - value) * size.height
            )
        )
    }
}

@Composable
private fun HuePicker(
    hue: Float,
    onHueChanged: (Float) -> Unit
) {
    val hueColors = remember {
        listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { position ->
                        onHueChanged(
                            (position.x / size.width * 360f)
                                .coerceIn(0f, 360f)
                        )
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onHueChanged(
                            (change.position.x / size.width * 360f)
                                .coerceIn(0f, 360f)
                        )
                    }
                )
            }
    ) {
        drawRect(
            brush = Brush.horizontalGradient(hueColors)
        )

        drawCircle(
            color = Color.White,
            radius = 10.dp.toPx(),
            center = Offset(
                x = hue / 360f * size.width,
                y = size.height / 2f
            )
        )

        drawCircle(
            color = Color.Black,
            radius = 7.dp.toPx(),
            center = Offset(
                x = hue / 360f * size.width,
                y = size.height / 2f
            )
        )
    }
}

@Composable
private fun ColorPreview(color: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        if (color.isBlank()) {
            CheckerboardBackground(modifier = Modifier.matchParentSize())
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(color.toColorInt()))
            )
        }
    }
}

@Composable
private fun CheckerboardBackground(modifier: Modifier, tileSize: Float = 12F) {
    Canvas(modifier = modifier) {
        val lightTile = Color(0xFFE0E0E0)
        val darkTile = Color(0xFFBDBDBD)
        val tileSizeInternal = tileSize.dp.toPx()

        val columns = (size.width / tileSizeInternal).toInt() + 1
        val rows = (size.height / tileSizeInternal).toInt() + 1

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val color = if ((row + column) % 2 == 0) {
                    lightTile
                } else {
                    darkTile
                }

                drawRect(
                    color = color,
                    topLeft = Offset(x = column * tileSizeInternal, y = row * tileSizeInternal),
                    size = Size(width = tileSizeInternal, height = tileSizeInternal)
                )
            }
        }
    }
}

private fun updateSaturationValue(
    position: Offset,
    width: Int,
    height: Int,
    onUpdated: (Pair<Float, Float>) -> Unit
) {
    val saturation = (position.x / width.toFloat()).coerceIn(0f, 1f)
    val value = 1f - (position.y / height.toFloat()).coerceIn(0f, 1f)

    onUpdated(saturation to value)
}

private fun Color.toHexString(): String {
    return String.format(
        Locale.US,
        "#%06X",
        toArgb() and 0xFFFFFF
    )
}
