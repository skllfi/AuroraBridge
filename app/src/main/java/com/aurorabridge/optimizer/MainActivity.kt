package com.aurorabridge.optimizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aurorabridge.optimizer.ui.theme.AuroraTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aurorabridge.optimizer.ui.screens.HomeScreen
import com.aurorabridge.optimizer.ui.screens.DiagnosticsScreen
import com.aurorabridge.optimizer.ui.screens.AdbCompanionScreen
import com.aurorabridge.optimizer.ui.screens.AppListScreen
import com.aurorabridge.optimizer.ui.screens.SettingsScreen
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.aurorabridge.optimizer.services.AppMonitorWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // schedule periodic worker every 12 hours
        val workRequest = PeriodicWorkRequestBuilder<AppMonitorWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(workRequest)

        setContent {
            AuroraTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("diagnostics") { DiagnosticsScreen(navController) }
                    composable("adb") { AdbCompanionScreen(navController) }
                    composable("apps") { AppListScreen(navController) }
                    composable("settings") { SettingsScreen(navController) }
                }
            }
        }
    }
}
