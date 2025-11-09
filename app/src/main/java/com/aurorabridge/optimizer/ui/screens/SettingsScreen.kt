package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.AppScreen
import com.aurorabridge.optimizer.ui.screens.settings.EnhancedConfirmationDialog
import com.aurorabridge.optimizer.ui.screens.settings.SettingsCard
import com.aurorabridge.optimizer.ui.screens.settings.OptimizationProfileCard
import com.aurorabridge.optimizer.ui.vm.SettingsUiState
import com.aurorabridge.optimizer.ui.vm.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val showDialog by settingsViewModel.showConfirmationDialog.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        settingsViewModel.loadInitialState()
        settingsViewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is SettingsUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is SettingsUiState.Loaded -> {
                    state.profile?.let {
                        OptimizationProfileCard(
                            profile = it,
                            onRunOptimization = { settingsViewModel.onRunOptimizationClicked() })
                    } ?: run {
                        Text(text = stringResource(R.string.settings_no_profile_found))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard(
                        icon = Icons.Default.History,
                        title = stringResource(R.string.settings_backup_history_title),
                        summary = stringResource(R.string.settings_backup_history_summary),
                        onClick = { navController.navigate(AppScreen.BackupHistory.route) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard(
                        icon = Icons.Default.Storage,
                        title = stringResource(R.string.profile_management_title),
                        summary = stringResource(R.string.profile_management_summary),
                        onClick = { navController.navigate(AppScreen.ProfileManagement.route) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard(
                        icon = Icons.Default.PowerSettingsNew,
                        title = stringResource(R.string.settings_auto_optimize_title),
                        summary = stringResource(R.string.settings_auto_optimize_summary),
                        onCheckedChange = { settingsViewModel.onAutoOptimizeOnStartupChanged(it) },
                        isChecked = state.autoOptimizeOnStartup
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.settings_safe_mode_title),
                        summary = stringResource(R.string.settings_safe_mode_summary),
                        onCheckedChange = { settingsViewModel.onSafeModeChanged(it) },
                        isChecked = state.safeModeEnabled
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard(
                        icon = Icons.Default.Code,
                        title = stringResource(id = R.string.new_features_toggle_title),
                        summary = stringResource(id = R.string.new_features_toggle_summary),
                        onCheckedChange = { settingsViewModel.onNewFeaturesEnabledChanged(it) },
                        isChecked = state.newFeaturesEnabled
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard(
                        icon = Icons.Default.Code,
                        title = stringResource(R.string.settings_dev_tools_title),
                        summary = stringResource(R.string.settings_dev_tools_summary),
                        onClick = { navController.navigate(AppScreen.CommandLogger.route) }
                    )

                    if (showDialog) {
                        EnhancedConfirmationDialog(
                            parsedCommands = state.parsedCommands,
                            onConfirm = { settingsViewModel.onConfirmOptimization() },
                            onDismiss = { settingsViewModel.onDismissDialog() }
                        )
                    }
                }
            }
        }
    }
}
