package com.aurorabridge.optimizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aurorabridge.optimizer.model.DeviceBrand
import com.aurorabridge.optimizer.ui.apps.AppManagerScreen
import com.aurorabridge.optimizer.ui.instructions.InstructionsScreen
import com.aurorabridge.optimizer.ui.screens.AdbActivationGuide
import com.aurorabridge.optimizer.ui.screens.AdbCompanionScreen
import com.aurorabridge.optimizer.ui.screens.AppControlScreen
import com.aurorabridge.optimizer.ui.screens.CommandLoggerScreen
import com.aurorabridge.optimizer.ui.screens.DiagnosticsScreen
import com.aurorabridge.optimizer.ui.screens.HomeScreen
import com.aurorabridge.optimizer.ui.screens.SettingsScreen
import com.aurorabridge.optimizer.ui.screens.UserWarningScreen
import com.aurorabridge.optimizer.ui.theme.AuroraTheme
import com.aurorabridge.optimizer.ui.vm.LanguageViewModel
import com.aurorabridge.optimizer.utils.AdbCommander
import com.aurorabridge.optimizer.utils.BrandAutoOptimizer
import com.aurorabridge.optimizer.utils.LocaleManager
import com.aurorabridge.optimizer.utils.OptimizationNotificationHelper
import com.aurorabridge.optimizer.utils.SettingsManager
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val resourceId: Int, val icon: Int) {
    object Home : Screen("home", R.string.home_title, R.drawable.ic_home)
    object AppManager : Screen("app_manager", R.string.app_manager_title, R.drawable.ic_apps)
    object Settings : Screen("settings", R.string.settings_title, R.drawable.ic_settings)
}

class MainActivity : ComponentActivity() {

    private val localeManager by lazy { LocaleManager() }
    private val notificationHelper by lazy { OptimizationNotificationHelper(this) }
    private val bottomNavItems = listOf(Screen.Home, Screen.AppManager, Screen.Settings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationHelper.createNotificationChannel()

        val language = localeManager.getLanguage(this)
        localeManager.setLocale(this, language)

        val languageViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return LanguageViewModel(localeManager) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            AuroraTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            bottomNavItems.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(painterResource(id = screen.icon), contentDescription = null) },
                                    label = { Text(stringResource(screen.resourceId)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "user_warning",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Routes from original MainActivity
                        composable("user_warning") { UserWarningScreen(navController) }
                        composable("diagnostics") { DiagnosticsScreen(navController) }
                        composable("adb") { AdbCompanionScreen(navController) }
                        composable("adb_guide") { AdbActivationGuide(navController) }
                        composable("instructions") { InstructionsScreen() }
                        composable("command_logger") { CommandLoggerScreen() } // Added route
                        composable(
                            "app_control_screen/{packageName}",
                            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            AppControlScreen(
                                navController = navController,
                                packageName = backStackEntry.arguments?.getString("packageName") ?: ""
                            )
                        }

                        // Routes from ui/MainActivity
                        composable(Screen.Home.route) { HomeScreen(navController) }
                        composable(Screen.Settings.route) { SettingsScreen(navController, languageViewModelFactory) }
                        composable(Screen.AppManager.route) { AppManagerScreen(navController) }
                    }
                }
            }
        }

        runAutoOptimization()
    }

    private fun runAutoOptimization() {
        val settingsManager = SettingsManager(this)
        if (settingsManager.isAutoOptimizeOnStartupEnabled()) {
            val brandOptimizer = BrandAutoOptimizer()
            val deviceBrand = brandOptimizer.getDeviceBrand()
            if (deviceBrand != DeviceBrand.UNKNOWN) {
                val commands = brandOptimizer.getOptimizationCommands(deviceBrand, "", this)
                val adbCommander = AdbCommander()
                lifecycleScope.launch {
                    var allSucceeded = true
                    for (command in commands) {
                        val result = adbCommander.runAdbCommandAsync(command)
                        if (!result.isSuccess) {
                            allSucceeded = false
                            break
                        }
                    }
                    notificationHelper.showAutoOptimizationNotification(allSucceeded)
                }
            }
        }
    }
}
