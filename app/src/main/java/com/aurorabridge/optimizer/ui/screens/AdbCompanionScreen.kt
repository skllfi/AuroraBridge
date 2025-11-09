package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.vm.AdbCompanionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdbCompanionScreen(navController: NavController, adbCompanionViewModel: AdbCompanionViewModel = viewModel()) {
    val command by adbCompanionViewModel.command.collectAsState()
    val output by adbCompanionViewModel.output.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.home_adb_companion)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TextField(
                value = command,
                onValueChange = { adbCompanionViewModel.onCommandChanged(it) },
                label = { Text(stringResource(R.string.text_field_label_command)) }
            )
            Button(onClick = { adbCompanionViewModel.runCommand() }) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.button_run))
            }
            TextField(
                value = output,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.text_field_label_output)) }
            )
        }
    }
}
