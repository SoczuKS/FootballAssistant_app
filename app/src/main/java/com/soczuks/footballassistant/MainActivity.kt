package com.soczuks.footballassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.soczuks.footballassistant.ui.navigation.NavGraph
import com.soczuks.footballassistant.ui.theme.FootballAssistantTheme
import com.soczuks.footballassistant.update.UpdateDialog
import com.soczuks.footballassistant.update.UpdateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var updateManager: UpdateManager

    override fun onResume() {
        super.onResume()
        updateManager.checkForUpdate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val updateState by updateManager.state.collectAsState()
            val unknownSourceLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
                    if (updateManager.hasInstallPermission()) {
                        updateManager.install(this@MainActivity)
                    }
                }

            var authenticatedEvent by remember { mutableIntStateOf(0) }
            val useDarkTheme = isSystemInDarkTheme()

            CompositionLocalProvider {
                FootballAssistantTheme(darkTheme = useDarkTheme) {
                    val navController = rememberNavController()

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(
                            navController = navController,
                            onAuthenticated = { authenticatedEvent++ })
                    }
                }
            }

            UpdateDialog(
                state = updateState,
                onDownload = updateManager::download,
                onInstall = {
                    if (updateManager.hasInstallPermission()) {
                        updateManager.install(this@MainActivity)
                    } else {
                        unknownSourceLauncher.launch(updateManager.unknownSourcesIntent())
                    }
                },
                onDismiss = updateManager::dismiss,
                onCheckAgain = { updateManager.checkForUpdate(force = true) }
            )
        }
    }
}
