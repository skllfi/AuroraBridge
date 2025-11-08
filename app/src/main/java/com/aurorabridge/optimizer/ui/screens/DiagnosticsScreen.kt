package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.vm.DiagnosticsUiState
import com.aurorabridge.optimizer.ui.vm.DiagnosticsViewModel

@Composable
fun DiagnosticsScreen(
    navController: NavController,
    diagnosticsViewModel: DiagnosticsViewModel = viewModel()
) {
    val uiState by diagnosticsViewModel.uiState.collectAsState()
    var showExportDialog by remember { mutableStateOf(false) }
    var reportContent by remember { mutableStateOf("") }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(stringResource(R.string.diagnostics_report_title)) },
            text = { Text(reportContent) },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text(stringResource(R.string.diagnostics_close_button))
                }
            }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.diagnostics_title)) }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.diagnostics_warning_title),
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.diagnostics_warning_message)
                    )
                }
            }

            when (val state = uiState) {
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
                            Text(stringResource(R.string.diagnostics_found_limiters, state.report.foundLimiters.joinToString(", ")))
                            Spacer(Modifier.height(16.dp))
                        }
                        items(state.report.rawOutputs.size) { index ->
                            val (key, value) = state.report.rawOutputs.entries.elementAt(index)
                            Text("$key: $value")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            reportContent = diagnosticsViewModel.exportReport(state.report)
                            showExportDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.diagnostics_export_button))
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
