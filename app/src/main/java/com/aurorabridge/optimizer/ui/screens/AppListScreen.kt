package com.aurorabridge.optimizer.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.vm.AppListUiState
import com.aurorabridge.optimizer.ui.vm.AppListViewModel
import com.aurorabridge.optimizer.ui.vm.AppInfo

@Composable
fun AppListScreen(
    navController: NavController,
    appListViewModel: AppListViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by appListViewModel.uiState.collectAsState()

    // Load apps when the composable is first launched
    LaunchedEffect(Unit) {
        appListViewModel.loadApps(context.packageManager)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_list_title)) }) }
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
                        AppListView(apps = state.apps, context = context)
                    }
                }
                is AppListUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// A new composable for the app list view
@Composable
private fun AppListView(apps: List<AppInfo>, context: Context) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(apps) { app ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { openAppSettings(context, app.packageName) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = app.name, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { openAppSettings(context, app.packageName) }) {
                        Text(stringResource(R.string.app_list_settings_button))
                    }
                }
            }
        }
    }
}

// Function to open the specific settings screen for a given app
private fun openAppSettings(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    context.startActivity(intent)
}
