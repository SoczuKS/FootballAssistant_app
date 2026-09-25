package com.soczuks.footballassistant.ui.competition.addcompetitionscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.soczuks.footballassistant.R
import com.soczuks.footballassistant.ui.competition.CompetitionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCompetitionScreen(
    onNavigateBack: () -> Unit,
    onCompetitionCreated: () -> Unit,
    viewModel: CompetitionViewModel = hiltViewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }

    val createCompetitionState by viewModel.createCompetitionState.collectAsState()

    LaunchedEffect(createCompetitionState) {
        if (createCompetitionState is CreateCompetitionState.Success) {
            onCompetitionCreated()
            onNavigateBack()
            viewModel.resetCreateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_competition_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.competition_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                viewModel.addCompetition(name)
            }, modifier = Modifier.fillMaxWidth(), enabled = name.isNotBlank() && createCompetitionState !is CreateCompetitionState.Loading) {
                if (createCompetitionState is CreateCompetitionState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.create_competition_button))
                }
            }

            if (createCompetitionState is CreateCompetitionState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (createCompetitionState as CreateCompetitionState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
