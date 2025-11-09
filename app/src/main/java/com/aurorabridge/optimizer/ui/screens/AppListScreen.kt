package com.aurorabridge.optimizer.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.AppScreen
import com.aurorabridge.optimizer.ui.components.ConfirmationDialog
import com.aurorabridge.optimizer.ui.vm.AppInfo
import com.aurorabridge.optimizer.ui.vm.AppListUiState
import com.aurorabridge.optimizer.ui.vm.AppListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    navController: NavController,
    profile: String?,
    appListViewModel: AppListViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by appListViewModel.uiState.collectAsState()
    val isSelectionModeActive by appListViewModel.isSelectionModeActive.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val snackbarMessage by appListViewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                appListViewModel.onSnackbarShown()
            }
        }
    }

    LaunchedEffect(Unit) {
        appListViewModel.loadApps(context.packageManager, context, profile)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_list_title)) },
                navigationIcon = {
                    if (isSelectionModeActive) {
                        IconButton(onClick = { appListViewModel.exitSelectionMode() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Exit Selection Mode")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when (val state = uiState) {
                is AppListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is AppListUiState.Success -> {
                    if (state.apps.isEmpty()) {
                        Text(stringResource(R.string.app_list_no_apps))
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            AppListView(
                                modifier = Modifier.weight(1f),
                                apps = state.apps,
                                context = context,
                                navController = navController,
                                appListViewModel = appListViewModel,
                                isSelectionModeActive = isSelectionModeActive
                            )
                            if (isSelectionModeActive) {
                                Spacer(modifier = Modifier.height(16.dp))
                                ActionButtons(
                                    appListViewModel = appListViewModel,
                                    state = state,
                                    context = context
                                )
                            }
                        }
                    }
                }

                is AppListUiState.ConfirmAction -> {
                    ConfirmationDialog(
                        commands = state.commands,
                        onConfirm = {
                            state.action()
                            appListViewModel.onConfirmationDialogDismissed()
                        },
                        onDismiss = { appListViewModel.onConfirmationDialogDismissed() }
                    )
                }

                is AppListUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    appListViewModel: AppListViewModel,
    state: AppListUiState.Success,
    context: Context
) {
    var showMenu by remember { mutableStateOf(false) }
    val availableProfiles = appListViewModel.getAvailableProfiles()

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        FilledTonalButton(onClick = { appListViewModel.disableBatteryOptimizationForSelectedApps() }) {
            Icon(Icons.Default.BatteryAlert, contentDescription = "Disable Battery Optimization")
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.app_list_disable_optimization_button))
        }
        FilledTonalButton(onClick = { appListViewModel.uninstallSelectedApps() }) {
            Icon(Icons.Default.Delete, contentDescription = "Uninstall")
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.app_list_uninstall_button))
        }
        Box {
            val recommendedProfile = state.recommendedProfile
            if (recommendedProfile != null) {
                FilledTonalButton(onClick = { appListViewModel.applyFixProfileForSelectedApps(context, recommendedProfile) }) {
                    Text("Apply $recommendedProfile")
                }
            } else {
                FilledTonalButton(onClick = { showMenu = true }) {
                    Text(stringResource(R.string.app_list_apply_fix_profile_button))
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    availableProfiles.forEach { profile ->
                        DropdownMenuItem(onClick = {
                            appListViewModel.applyFixProfileForSelectedApps(context, profile)
                            showMenu = false
                        }, text = { Text(profile) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppListView(
    modifier: Modifier = Modifier,
    apps: List<AppInfo>,
    context: Context,
    navController: NavController,
    appListViewModel: AppListViewModel,
    isSelectionModeActive: Boolean
) {
    LazyColumn(modifier = modifier) {
        items(apps) { app ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { appListViewModel.activateSelectionAndToggle(app) },
                            onTap = {
                                if (isSelectionModeActive) {
                                    appListViewModel.toggleAppSelection(app)
                                }
                            }
                        )
                    },
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (isSelectionModeActive) {
                        Checkbox(checked = app.isSelected, onCheckedChange = { appListViewModel.toggleAppSelection(app) })
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(text = app.name, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { openAppSettings(context, app.packageName) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.app_list_settings_button))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { navController.navigate(AppScreen.AppControl.createRoute(app.packageName)) }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.app_list_manage_button))
                    }
                }
            }
        }
    }
}

private fun openAppSettings(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    context.startActivity(intent)
}
