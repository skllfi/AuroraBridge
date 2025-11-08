package com.aurorabridge.optimizer.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R

data class Step(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    @StringRes val buttonTextResId: Int? = null,
    val action: ((Context) -> Unit)? = null
)

private fun getSteps(context: Context): List<Step> = listOf(
    Step(
        titleResId = R.string.adb_guide_step1_title,
        descriptionResId = R.string.adb_guide_step1_desc,
        buttonTextResId = R.string.adb_guide_open_settings,
        action = { ctx -> ctx.startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)) }
    ),
    Step(
        titleResId = R.string.adb_guide_step2_title,
        descriptionResId = R.string.adb_guide_step2_desc
    ),
    Step(
        titleResId = R.string.adb_guide_step3_title,
        descriptionResId = R.string.adb_guide_step3_desc,
        buttonTextResId = R.string.adb_guide_open_settings,
        action = { ctx -> ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)) }
    )
)

@Composable
fun AdbActivationGuide(navController: NavController) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(0) }
    val showSkipDialog = remember { mutableStateOf(false) }

    val steps = remember(context) { getSteps(context) }

    val areDeveloperOptionsEnabled = Settings.Global.getInt(
        context.contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
    ) == 1

    if (areDeveloperOptionsEnabled && !showSkipDialog.value) {
        LaunchedEffect(Unit) {
            showSkipDialog.value = true
        }
    }

    if (showSkipDialog.value) {
        AlertDialog(
            onDismissRequest = { showSkipDialog.value = false },
            title = { Text(stringResource(R.string.adb_guide_dev_options_enabled_title)) },
            text = { Text(stringResource(R.string.adb_guide_dev_options_enabled_desc)) },
            confirmButton = {
                Button(
                    onClick = {
                        showSkipDialog.value = false
                        currentStep = steps.size - 1
                    }
                ) {
                    Text(stringResource(R.string.adb_guide_skip_to_last_step))
                }
            },
            dismissButton = {
                Button(onClick = { showSkipDialog.value = false }) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    Button(onClick = { currentStep-- }) { Text(stringResource(R.string.adb_guide_previous)) }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (currentStep < steps.size - 1) {
                    Button(onClick = { currentStep++ }) { Text(stringResource(R.string.adb_guide_next)) }
                }
                if (currentStep == steps.size - 1) {
                    Button(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.adb_guide_finish)) }
                }
            }
        }
    }
}
