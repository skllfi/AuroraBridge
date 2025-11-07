package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aurorabridge.optimizer.adb.AdbConnectionManager
import com.aurorabridge.optimizer.adb.AdbProfiles
import com.aurorabridge.optimizer.adb.AdbOptimizer
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun AdbCompanionScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    var status by remember { mutableStateOf("Unknown") }
    var results by remember { mutableStateOf(mapOf<String,String>()) }

    LaunchedEffect(Unit) {
        status = if (AdbConnectionManager.isAdbWifiEnabled()) "ADB Wi-Fi: enabled" else "ADB Wi-Fi: disabled"
    }

    Scaffold(topBar = { TopAppBar(title = { Text("ADB Companion") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(status)
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                val ok = AdbConnectionManager.enableAdbWifi()
                Toast.makeText(context, if (ok) "Enabled ADB Wi-Fi (try adb connect <ip>:5555)" else "Failed to enable ADB Wi-Fi", Toast.LENGTH_LONG).show()
                status = if (AdbConnectionManager.isAdbWifiEnabled()) "ADB Wi-Fi: enabled" else "ADB Wi-Fi: disabled"
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Enable ADB Wi-Fi")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val ok = AdbConnectionManager.disableAdbWifi()
                Toast.makeText(context, if (ok) "Disabled ADB Wi-Fi" else "Failed to disable ADB Wi-Fi", Toast.LENGTH_LONG).show()
                status = if (AdbConnectionManager.isAdbWifiEnabled()) "ADB Wi-Fi: enabled" else "ADB Wi-Fi: disabled"
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Disable ADB Wi-Fi")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                // run universal profile
                results = withContext(Dispatchers.IO) { AdbOptimizer.runCommands(AdbProfiles.UniversalFix) }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Run UniversalFix Profile")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                results = withContext(Dispatchers.IO) { AdbOptimizer.runCommands(AdbProfiles.HuaweiFix) }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Run HuaweiFix Profile")
            }
            Spacer(Modifier.height(12.dp))
            Text("Last results: ")
            for ((cmd, out) in results) {
                Text(text = cmd + ": " + out.take(120))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onBack) { Text("Back") }
        }
    }
}
