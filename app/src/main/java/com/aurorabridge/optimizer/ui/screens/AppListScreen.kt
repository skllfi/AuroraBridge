package com.aurorabridge.optimizer.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

@Composable
fun AppListScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    val pm = context.packageManager
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack) { Text("Back") }
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(apps) { app ->
                AppRow(app.loadLabel(pm).toString(), app.packageName)
            }
        }
    }
}

@Composable
fun AppRow(label: String, packageName: String) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label)
            Text(text = packageName)
        }
        Column {
            Button(onClick = {
                val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                i.data = android.net.Uri.parse("package:" + packageName)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }) {
                Text("App settings")
            }
        }
    }
}
