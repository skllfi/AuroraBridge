package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.viewmodel.DiagnosticsViewModel

@Composable
fun AdbAnalyzerScreen(
    navController: NavController,
    viewModel: DiagnosticsViewModel = viewModel()
) {
    val analysisResult by viewModel.analysisResult
    val isAnalyzing by viewModel.isAnalyzing
    val errorMessage by viewModel.errorMessage

    Scaffold(
        topBar = { TopAppBar(title = { Text("ADB Analyzer") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "System Limiter Analysis",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.runAnalysis() },
                enabled = !isAnalyzing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Run Analysis")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isAnalyzing) {
                CircularProgressIndicator()
            } else {
                errorMessage?.let {
                    Text(
                        text = "Error: $it",
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (analysisResult.isNotEmpty()) {
                    Text(
                        text = "Found Limiters:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(analysisResult) { limiterName ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(
                                    text = limiterName,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Don't show this message if there was an error
                    if (errorMessage == null) {
                        Text("Analysis has not been run or no limiters were found.")
                    }
                }
            }
        }
    }
}
