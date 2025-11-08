package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.Limiter
import com.aurorabridge.optimizer.ui.components.UserWarningDialog
import com.aurorabridge.optimizer.ui.vm.DiagnosticsUiState
import com.aurorabridge.optimizer.ui.vm.DiagnosticsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DiagnosticsScreen(
    navController: NavController,
    diagnosticsViewModel: DiagnosticsViewModel = viewModel()
) {
    val uiState by diagnosticsViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        diagnosticsViewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.diagnostics_title)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = uiState) {
                is DiagnosticsUiState.RequiresConfirmation -> {
                    UserWarningDialog(
                        title = stringResource(R.string.diagnostics_warning_title),
                        message = stringResource(R.string.diagnostics_warning_message),
                        onConfirm = { diagnosticsViewModel.onWarningConfirmed() },
                        onDismiss = { navController.popBackStack() }
                    )
                }
                is DiagnosticsUiState.Idle -> {
                    Button(onClick = { diagnosticsViewModel.runDiagnostics() }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.diagnostics_run_button))
                    }
                }
                is DiagnosticsUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is DiagnosticsUiState.Success -> {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Diagnostics Report", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Button(onClick = { diagnosticsViewModel.shareReport(context, state.report) }) {
                                    Text(stringResource(R.string.diagnostics_export_button))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            LimiterSection(limiters = state.report.foundLimiters, onApplyFix = { diagnosticsViewModel.applyFix(it, context) })
                        }
                        item {
                            ReportSection(title = "Logcat Errors", items = state.report.logcatErrors)
                        }
                        item {
                            ReportSection(title = "Top 5 Battery Hogs", items = state.report.batteryHogs.map { "${it.first}: ${it.second}%" })
                        }
                    }
                }
                is DiagnosticsUiState.Error -> {
                    Text(state.message, color = Color.Red)
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.diagnostics_back_button)) }
        }
    }
}

@Composable
private fun LimiterSection(limiters: List<Limiter>, onApplyFix: (Limiter) -> Unit) {
    if (limiters.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(text = "Found Limiters", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            limiters.forEach { limiter ->
                Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(limiter.name), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            limiter.fixCommand?.let {
                                Button(onClick = { onApplyFix(limiter) }) {
                                    Text("Fix")
                                }
                            }
                        }
                        Text(text = stringResource(limiter.description))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportSection(title: String, items: List<String>) {
    if (items.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    items.forEach { item ->
                        Text(text = item, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
            }
        }
    }
}