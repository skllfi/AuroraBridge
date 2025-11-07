package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.aurorabridge.optimizer.utils.LogUtils
import com.aurorabridge.optimizer.utils.BrandDetectionUtils

@Composable
fun SettingsScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    var lang by remember { mutableStateOf("en") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack) { Text("Back") }
        Spacer(Modifier.height(12.dp))
        Text("Select language:")
        Row {
            RadioButton(selected = lang=="en", onClick = { lang = "en" })
            Text("English")
            Spacer(Modifier.width(16.dp))
            RadioButton(selected = lang=="ru", onClick = { lang = "ru" })
            Text("Русский")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            // export logs
            val basePath = context.filesDir.absolutePath
            LogUtils.saveReport(basePath, mapOf("brand" to BrandDetectionUtils.getManufacturer()))
            Toast.makeText(context, "Saved logs to $basePath", Toast.LENGTH_LONG).show()
        }) {
            Text("Export diagnostics/logs")
        }
    }
}
