package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.AppCategory
import com.aurorabridge.optimizer.model.AppItem
import com.aurorabridge.optimizer.ui.vm.AppManagerUiState
import com.aurorabridge.optimizer.ui.vm.AppManagerViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppManagerScreen(
    navController: NavController,
    viewModel: AppManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val successState = uiState as? AppManagerUiState.Success

    Scaffold(
        topBar = {
            if (successState?.isSelectionModeActive == true) {
                SelectionTopAppBar(viewModel = viewModel, selectedCount = successState.selectedApps.size)
            } else {
                DefaultTopAppBar(viewModel = viewModel)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (successState?.isSelectionModeActive == false) {
                FloatingActionButton(onClick = { viewModel.enterSelectionMode() }) {
                    Icon(Icons.Default.Check, contentDescription = "Enter Selection Mode")
                }
            }
        },
        bottomBar = {
            if (successState?.isSelectionModeActive == true) {
                SelectionActionsBottomBar(viewModel = viewModel)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is AppManagerUiState.Loading -> {
                    Arrangement.Center
                    CircularProgressIndicator()
                }
                is AppManagerUiState.Error -> {
                    Arrangement.Center
                    Text(state.message)
                }
                is AppManagerUiState.Success -> {
                    LaunchedEffect(state.message) {
                        state.message?.let {
                            snackbarHostState.showSnackbar(it)
                            viewModel.messageShown()
                        }
                    }
                    FilterChips(activeFilters = state.activeFilters, onFilterToggle = viewModel::toggleFilter)
                    AppList(navController = navController, apps = state.apps, viewModel = viewModel, successState = state)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(viewModel: AppManagerViewModel) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_manager_title)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopAppBar(viewModel: AppManagerViewModel, selectedCount: Int) {
    TopAppBar(
        title = { Text("$selectedCount selected") },
        navigationIcon = {
            IconButton(onClick = { viewModel.exitSelectionMode() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit selection mode")
            }
        }
    )
}

@Composable
fun FilterChips(activeFilters: Set<AppCategory>, onFilterToggle: (AppCategory) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AppCategory.values().forEach { category ->
            FilterChip(
                selected = activeFilters.contains(category),
                onClick = { onFilterToggle(category) },
                label = { Text(category.name) }
            )
        }
    }
}

@Composable
fun AppList(navController: NavController, apps: List<AppItem>, viewModel: AppManagerViewModel, successState: AppManagerUiState.Success) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(apps, key = { it.packageName }) { app ->
            AppListItem(navController = navController, app = app, viewModel = viewModel, successState = successState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(navController: NavController, app: AppItem, viewModel: AppManagerViewModel, successState: AppManagerUiState.Success) {
    var showMenu by remember { mutableStateOf(false) }
    val isSelected = successState.selectedApps.contains(app.packageName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { 
                    if (successState.isSelectionModeActive) {
                        viewModel.toggleAppSelection(app.packageName) 
                    } else {
                        // No-op
                    }
                },
                onLongClick = { viewModel.toggleAppSelection(app.packageName) }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (successState.isSelectionModeActive) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { viewModel.toggleAppSelection(app.packageName) },
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Image(
            painter = rememberDrawablePainter(drawable = app.icon),
            contentDescription = app.name,
            modifier = Modifier.size(40.dp)
        )
        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
            Text(text = app.name)
            Text(text = app.packageName, style = MaterialTheme.typography.bodySmall)
        }

        if (!successState.isSelectionModeActive) {
             IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More actions")
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Permissions") },
                        leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null)},
                        onClick = { 
                            navController.navigate("permissions/${app.packageName}")
                            showMenu = false 
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Uninstall")}, 
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null)},
                        onClick = { 
                            viewModel.uninstallApp(app.packageName)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Disable")}, 
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null)},
                        onClick = { 
                            viewModel.disableApp(app.packageName) 
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = {Text("Clear cache")}, 
                        leadingIcon = { Icon(Icons.Default.CleaningServices, contentDescription = null)},
                        onClick = { 
                            viewModel.clearCache(app.packageName)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionActionsBottomBar(viewModel: AppManagerViewModel) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.uninstallSelectedApps() }) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Uninstall")
            }
            Button(onClick = { viewModel.disableSelectedApps() }) {
                Icon(Icons.Default.Block, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disable")
            }
        }
    }
}
