package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Button(onClick = { navController.navigate("adb_guide") }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_instructions))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("diagnostics") }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_diagnostics))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("adb") }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_adb_companion))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("apps") }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_app_list))
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("settings") }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_settings))
            }
        }
    }
}
