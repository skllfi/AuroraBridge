package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.optimizer.OptimizationProfile
import com.aurorabridge.optimizer.ui.components.ConfirmationDialog
import com.aurorabridge.optimizer.ui.vm.LanguageViewModel
import com.aurorabridge.optimizer.ui.vm.SettingsUiState
import com.aurorabridge.optimizer.ui.vm.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    languageViewModelFactory: ViewModelProvider.Factory,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val showDialog by settingsViewModel.showConfirmationDialog.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        settingsViewModel.loadInitialState(context)
        settingsViewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is SettingsUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is SettingsUiState.Loaded -> {
                    state.profile?.let {
                        OptimizationProfileCard(profile = it, onRunOptimization = { settingsViewModel.onRunOptimizationClicked() })
                    } ?: run {
                        Text(text = "No optimization profile found for your device.")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BackupCard(
                        hasBackup = state.hasBackup,
                        onCreateBackup = { settingsViewModel.createBackup(context) },
                        onRestoreBackup = { settingsViewModel.restoreBackup(context) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AutoOptimizeCard(
                        autoOptimizeOnStartup = state.autoOptimizeOnStartup,
                        onCheckedChange = { settingsViewModel.onAutoOptimizeOnStartupChanged(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DevToolsCard(navController = navController)

                    if (showDialog) {
                        state.profile?.let {
                            ConfirmationDialog(
                                title = stringResource(id = R.string.settings_brand_optimization_dialog_title),
                                message = stringResource(id = R.string.settings_brand_optimization_dialog_message) + "\n\n" + it.commands.joinToString("\n"),
                                onConfirm = { settingsViewModel.onConfirmOptimization(context) },
                                onDismiss = { settingsViewModel.onDismissDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptimizationProfileCard(profile: OptimizationProfile, onRunOptimization: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = "Info")
                Spacer(modifier = Modifier.padding(start = 16.dp))
                Text(text = "Detected Brand: ${profile.brandName}", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(id = profile.description))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRunOptimization, modifier = Modifier.fillMaxWidth()) {
                Text("Run ${profile.brandName} Auto-Optimize")
            }
        }
    }
}

@Composable
private fun BackupCard(hasBackup: Boolean, onCreateBackup: () -> Unit, onRestoreBackup: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Backup & Restore", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Create a restore point before applying optimizations.")
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onCreateBackup) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text("Create Restore Point")
                }
                Button(onClick = onRestoreBackup, enabled = hasBackup) {
                    Icon(Icons.Default.Restore, contentDescription = "Restore")
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text("Restore from Backup")
                }
            }
        }
    }
}

@Composable
private fun AutoOptimizeCard(autoOptimizeOnStartup: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PowerSettingsNew, contentDescription = "Auto-optimize")
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(text = "Auto-Optimize on Startup", fontWeight = FontWeight.Bold)
                Text(text = "Apply the optimization profile automatically when your device starts.")
            }
            Switch(checked = autoOptimizeOnStartup, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun DevToolsCard(navController: NavController) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("command_logger") }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Code, contentDescription = "Developer Tools")
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(text = "Developer Tools", fontWeight = FontWeight.Bold)
                Text(text = "View command logs and other debug information.")
            }
        }
    }
}
