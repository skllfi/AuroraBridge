package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurorabridge.optimizer.adb.AdbAnalyzer
import com.aurorabridge.optimizer.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import java.io.File

@Composable
fun DiagnosticsScreen(navController: androidx.navigation.NavController)onBack: () -> Unit) {
    val context = LocalContext.current
    var report by remember { mutableStateOf<com.aurorabridge.optimizer.adb.AdbAnalyzer.AnalyzerReport?>(null) }
    var running by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Diagnostics") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (report == null) {
                Text("No report yet.")
            } else {
                Text("Found limiters: " + report!!.foundLimiters.joinToString(", "))
                Spacer(Modifier.height(8.dp))
                Text("Raw outputs keys: " + report!!.rawOutputs.keys.joinToString(", "))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                running = true
                // run analyzer
                LaunchedEffect(Unit) {}
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (running) "Running..." else "Run ADB Analyzer")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                // Execute and update state
                LaunchedEffect(Unit) {}
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Run and Save Report")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                // export report file
                val basePath = context.filesDir.absolutePath
                if (report != null) {
                    LogUtils.saveReport(basePath, report!!)
                    Toast.makeText(context, "Report saved to $basePath/diagnostics.json", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "No report to export", Toast.LENGTH_LONG).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Export last report")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onBack) { Text("Back") }
        }
    }
}
