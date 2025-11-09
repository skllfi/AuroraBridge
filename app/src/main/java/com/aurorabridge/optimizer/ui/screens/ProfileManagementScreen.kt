package com.aurorabridge.optimizer.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.vm.ProfileManagementUiState
import com.aurorabridge.optimizer.ui.vm.ProfileManagementViewModel

@Composable
fun ProfileManagementScreen(
    viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.importProfile(it) }
        }
    )

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.exportProfile(it) }
        }
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.profile_management_title)) }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = uiState) {
                is ProfileManagementUiState.Idle -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { importLauncher.launch("*/*") }) {
                            Text(stringResource(R.string.import_button_text))
                        }
                        Button(onClick = { exportLauncher.launch("optimization_profile.json") }) {
                            Text(stringResource(R.string.export_button_text))
                        }
                    }
                }
                is ProfileManagementUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is ProfileManagementUiState.Success -> {
                    Text(state.message)
                }
                is ProfileManagementUiState.Error -> {
                    Text(state.message)
                }
            }
        }
    }
}
