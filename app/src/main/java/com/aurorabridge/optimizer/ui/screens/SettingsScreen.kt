package com.aurorabridge.optimizer.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.adb.BrandOptimizerManager
import com.aurorabridge.optimizer.ui.vm.LanguageViewModel
import com.aurorabridge.optimizer.ui.vm.SettingsViewModel
import com.aurorabridge.optimizer.utils.BackupManager
import com.aurorabridge.optimizer.utils.BrandDetectionUtils

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel(),
    languageViewModel: LanguageViewModel = viewModel()
) {
    val context = LocalContext.current
    val brand = BrandDetectionUtils.getManufacturer()
    val currentActivity = LocalContext.current as? Activity

    LaunchedEffect(Unit) {
        settingsViewModel.loadSettings(context)
    }

    val isAutoStartEnabled by settingsViewModel.autoStartState.collectAsState()
    val isBackgroundWorkEnabled by settingsViewModel.backgroundWorkState.collectAsState()
    val currentLanguage = remember { languageViewModel.getLanguage(context) }
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    val onLanguageSelected: (String) -> Unit = { newLanguage ->
        if (selectedLanguage != newLanguage) {
            selectedLanguage = newLanguage
            languageViewModel.setLanguage(context, newLanguage)
            Toast.makeText(context, "Language updated. Restarting...", Toast.LENGTH_SHORT).show()
            currentActivity?.recreate()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.diagnostics_warning_title),
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.settings_warning_message, brand)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.settings_enable_autostart))
                Switch(
                    checked = isAutoStartEnabled,
                    onCheckedChange = { settingsViewModel.toggleAutoStart(it, context) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.settings_enable_background_work))
                Switch(
                    checked = isBackgroundWorkEnabled,
                    onCheckedChange = { settingsViewModel.toggleBackgroundWork(it, context) }
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.settings_detected_brand, brand), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Button(onClick = {
                BrandOptimizerManager.runBrandOptimization(context)
                Toast.makeText(context, stringResource(R.string.settings_brand_optimization_toast, brand), Toast.LENGTH_LONG).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_run_brand_optimization, brand))
            }

            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                val success = BackupManager.createBackup(context, mapOf("setting1" to "value1"))
                if (success) {
                    Toast.makeText(context, stringResource(R.string.settings_backup_created), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, stringResource(R.string.settings_backup_failed), Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_create_backup))
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                val restoredSettings = BackupManager.restoreBackup(context)
                if (restoredSettings != null) {
                    Toast.makeText(context, stringResource(R.string.settings_restore_success), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, stringResource(R.string.settings_restore_failed), Toast.LENGTH_SHORT).show()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_restore_backup))
            }

            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                val message = settingsViewModel.exportLogs(context, brand)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }) {
                Text(stringResource(R.string.settings_export_logs))
            }

            Spacer(Modifier.height(24.dp))
            Text("Language Selection", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = selectedLanguage == "en",
                    onClick = { onLanguageSelected("en") }
                )
                Text("English")
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = selectedLanguage == "ru",
                    onClick = { onLanguageSelected("ru") }
                )
                Text("Русский")
            }

            Spacer(Modifier.height(24.dp))

            Button(onClick = { navController.popBackStack() }) { Text(stringResource(R.string.diagnostics_back_button)) }
        }
    }
}
