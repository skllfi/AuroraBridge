package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Aurora Bridge") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Button(onClick = { navController.navigate("diagnostics") }, modifier = Modifier.fillMaxWidth()) {
                Text("Run Diagnostics")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("adb") }, modifier = Modifier.fillMaxWidth()) {
                Text("ADB Companion")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("apps") }, modifier = Modifier.fillMaxWidth()) {
                Text("App List")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("settings") }, modifier = Modifier.fillMaxWidth()) {
                Text("Brand Auto-Optimize / Settings")
            }
        }
    }
}
