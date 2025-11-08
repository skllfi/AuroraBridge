package com.aurorabridge.optimizer.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.adb.AdbProfiles
import com.aurorabridge.optimizer.ui.vm.AdbCompanionUiState
import com.aurorabridge.optimizer.ui.vm.AdbCompanionViewModel

@Composable
fun AdbCompanionScreen(
    navController: NavController,
    adbCompanionViewModel: AdbCompanionViewModel = viewModel()
) {
    val uiState by adbCompanionViewModel.uiState.collectAsState()
    val profileNames = remember { AdbProfiles.getProfileNames() }
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.adb_companion_title)) }) }) { padding ->
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
                        text = stringResource(R.string.adb_companion_warning_message)
                    )
                }
            }

            when (val state = uiState) {
                is AdbCompanionUiState.Idle, is AdbCompanionUiState.Success, is AdbCompanionUiState.Error -> {
                    if (state is AdbCompanionUiState.Success) {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    } else if (state is AdbCompanionUiState.Error) {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(profileNames.size) { index ->
                            val profileName = profileNames[index]
                            Button(
                                onClick = { adbCompanionViewModel.applyOptimizationProfile(profileName) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Text(stringResource(R.string.adb_companion_apply_profile_button, profileName))
                            }
                        }
                    }
                }
                is AdbCompanionUiState.Loading -> {
                    CircularProgressIndicator()
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { adbCompanionViewModel.enableAdbWifi() }) {
                    Text(stringResource(R.string.adb_companion_enable_wifi_button))
                }
                Button(onClick = { adbCompanionViewModel.disableAdbWifi() }) {
                    Text(stringResource(R.string.adb_companion_disable_wifi_button))
                }
            }

            Spacer(Modifier.weight(1f))
            Button(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.diagnostics_back_button)) }
        }
    }
}
