package com.aurorabridge.optimizer.ui.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppManagerScreen(viewModel: AppManagerViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadApps(context)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("App Manager") }) }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            ActionButtons(viewModel = viewModel, uiState = uiState)

            Spacer(modifier = Modifier.height(8.dp))

            FilterButtons(viewModel = viewModel, uiState = uiState)

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = uiState.output,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.apps) { app ->
                        AppListItem(app = app, isSelected = uiState.selectedApps.contains(app.packageName)) {
                            viewModel.toggleAppSelection(app.packageName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(viewModel: AppManagerViewModel, uiState: AppManagerUiState) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = { viewModel.uninstallSelectedApps() }, enabled = uiState.selectedApps.isNotEmpty()) { Text("Uninstall") }
        Button(onClick = { viewModel.disableSelectedApps() }, enabled = uiState.selectedApps.isNotEmpty()) { Text("Disable") }
        Button(onClick = { viewModel.enableSelectedApps() }, enabled = uiState.selectedApps.isNotEmpty()) { Text("Enable") }
    }
}

@Composable
private fun FilterButtons(viewModel: AppManagerViewModel, uiState: AppManagerUiState) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = { viewModel.setFilter(AppFilter.ALL) }, enabled = uiState.currentFilter != AppFilter.ALL) { Text("All") }
        Button(onClick = { viewModel.setFilter(AppFilter.USER_ONLY) }, enabled = uiState.currentFilter != AppFilter.USER_ONLY) { Text("User") }
        Button(onClick = { viewModel.setFilter(AppFilter.SYSTEM_ONLY) }, enabled = uiState.currentFilter != AppFilter.SYSTEM_ONLY) { Text("System") }
    }
}

@Composable
private fun AppListItem(app: AppInfo, isSelected: Boolean, onToggleSelection: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onToggleSelection() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = app.icon),
            contentDescription = null, // decorative
            modifier = Modifier.size(40.dp)
        )
        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
            Text(text = app.name)
            Text(text = app.packageName)
        }
        Checkbox(checked = isSelected, onCheckedChange = { onToggleSelection() })
    }
}
