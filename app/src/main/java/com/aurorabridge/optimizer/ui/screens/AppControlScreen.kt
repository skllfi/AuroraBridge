package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
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
import com.aurorabridge.optimizer.ui.vm.AppControlViewModel

@Composable
fun AppControlScreen(
    navController: NavController,
    packageName: String,
    appControlViewModel: AppControlViewModel = viewModel()
) {
    val context = LocalContext.current
    val appName = appControlViewModel.getAppName(context, packageName)
    val isBatteryOptimizationOff by appControlViewModel.isBatteryOptimizationOff.collectAsState()

    LaunchedEffect(packageName) {
        appControlViewModel.checkBatteryOptimizationStatus(context, packageName)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_control_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = appName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.BatteryChargingFull, contentDescription = "Battery Optimization")
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.app_control_disable_battery_optimization),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isBatteryOptimizationOff,
                    onCheckedChange = { isChecked ->
                        appControlViewModel.setBatteryOptimization(context, packageName, isChecked)
                    }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.diagnostics_back_button))
            }
        }
    }
}
