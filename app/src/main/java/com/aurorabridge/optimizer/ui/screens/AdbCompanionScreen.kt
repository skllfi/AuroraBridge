package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.adb.AdbProfiles
import com.aurorabridge.optimizer.ui.components.UserWarningDialog
import com.aurorabridge.optimizer.ui.vm.AdbCompanionUiState
import com.aurorabridge.optimizer.ui.vm.AdbCompanionViewModel
import kotlinx.coroutines.launch

@Composable
fun AdbCompanionScreen(
    navController: NavController,
    adbCompanionViewModel: AdbCompanionViewModel = viewModel()
) {
    val uiState by adbCompanionViewModel.uiState.collectAsState()
    val profileNames = remember { AdbProfiles.getProfileNames() }
    val showDialog by adbCompanionViewModel.showConfirmDialog.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    showDialog?.let { profileName ->
        AlertDialog(
            onDismissRequest = { adbCompanionViewModel.dismissDialog() },
            title = { Text(stringResource(R.string.adb_companion_confirm_dialog_title)) },
            text = { Text(stringResource(R.string.adb_companion_confirm_dialog_message, profileName)) },
            confirmButton = {
                Button(
                    onClick = { adbCompanionViewModel.confirmApplyOptimizationProfile(profileName) })
                {
                    Text(stringResource(R.string.adb_companion_confirm_dialog_confirm_button))
                }
            },
            dismissButton = {
                Button(onClick = { adbCompanionViewModel.dismissDialog() }) {
                    Text(stringResource(R.string.adb_companion_confirm_dialog_dismiss_button))
                }
            }
        )
    }

    LaunchedEffect(uiState) {
        (uiState as? AdbCompanionUiState.Success)?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it.message)
            }
        }
        (uiState as? AdbCompanionUiState.Error)?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it.message, withDismissAction = true)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.adb_companion_title)) }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            when (uiState) {
                AdbCompanionUiState.RequiresConfirmation -> {
                    UserWarningDialog(
                        title = stringResource(id = R.string.user_warning_title),
                        message = stringResource(id = R.string.adb_companion_warning_message),
                        onConfirm = { adbCompanionViewModel.onWarningConfirmed() },
                        onDismiss = { navController.popBackStack() })
                }

                else -> {

                    if (uiState is AdbCompanionUiState.Loading) {
                        CircularProgressIndicator()
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(profileNames.size) { index ->
                                val profileName = profileNames[index]
                                Button(
                                    onClick = { adbCompanionViewModel.applyOptimizationProfile(profileName) },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text(stringResource(R.string.adb_companion_apply_profile_button, profileName))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { adbCompanionViewModel.enableAdbWifi() }) {
                            Text(stringResource(R.string.adb_companion_enable_wifi_button))
                        }
                        Button(onClick = { adbCompanionViewModel.disableAdbWifi() }) {
                            Text(stringResource(R.string.adb_companion_disable_wifi_button))
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Button(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.diagnostics_back_button)) }
        }
    }
}
