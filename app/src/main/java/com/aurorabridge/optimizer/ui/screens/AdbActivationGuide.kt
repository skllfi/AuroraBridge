package com.aurorabridge.optimizer.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.* 
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
import com.aurorabridge.optimizer.ui.vm.AdbActivationGuideViewModel
import com.aurorabridge.optimizer.ui.vm.Step

@Composable
fun AdbActivationGuide(navController: NavController, adbViewModel: AdbActivationGuideViewModel = viewModel()) {
    val context = LocalContext.current
    val currentStep by adbViewModel.currentStep.collectAsState()
    val showSkipDialog by adbViewModel.showSkipDialog.collectAsState()
    val steps = adbViewModel.getSteps()

    LaunchedEffect(Unit) {
        adbViewModel.checkForDeveloperOptions(context)
    }

    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { adbViewModel.onSkipDialogDismissed() },
            title = { Text(stringResource(R.string.adb_guide_dev_options_enabled_title)) },
            text = { Text(stringResource(R.string.adb_guide_dev_options_enabled_desc)) },
            confirmButton = {
                Button(
                    onClick = { adbViewModel.onSkipToLastStep() }
                ) {
                    Text(stringResource(R.string.adb_guide_skip_to_last_step))
                }
            },
            dismissButton = {
                Button(onClick = { adbViewModel.onSkipDialogDismissed() }) {
                    Text(stringResource(R.string.adb_guide_stay_in_guide))
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.adb_guide_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(stringResource(steps[currentStep].titleResId), style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(steps[currentStep].descriptionResId), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(24.dp))

                    steps[currentStep].action?.let { action ->
                        steps[currentStep].buttonTextResId?.let { buttonTextId ->
                            Button(
                                onClick = { action(context) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(stringResource(buttonTextId))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { adbViewModel.onTroubleshooting() }, 
                    enabled = !steps[currentStep].isTroubleshooting
                ) {
                    Text(stringResource(R.string.troubleshooting_title))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    Button(onClick = { adbViewModel.onPreviousStep() }) { Text(stringResource(R.string.adb_guide_previous)) }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (currentStep < steps.size - 1 && !steps[currentStep].isTroubleshooting) {
                    Button(onClick = { adbViewModel.onNextStep() }) { Text(stringResource(R.string.adb_guide_next)) }
                } else {
                    Button(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.adb_guide_finish)) }
                }
            }
        }
    }
}
