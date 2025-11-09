package com.aurorabridge.optimizer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aurorabridge.optimizer.ui.apps.AppManagerScreen
import com.aurorabridge.optimizer.ui.apps.BackupHistoryRepository
import com.aurorabridge.optimizer.ui.instructions.InstructionsScreen
import com.aurorabridge.optimizer.ui.screens.*
import com.aurorabridge.optimizer.ui.vm.BackupHistoryViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    requestPermission: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppScreen.Onboarding.route) { OnboardingScreen(navController) }
        composable(AppScreen.Permissions.route) { 
            PermissionsScreen(navController) { 
                requestPermission()
            }
        }
        composable("permissions/{packageName}", arguments = listOf(navArgument("packageName") { type = NavType.StringType })) {
            PermissionsScreen(navController)
        }
        composable(AppScreen.UserWarning.route) { UserWarningScreen(navController) }
        composable(AppScreen.Diagnostics.route) { DiagnosticsScreen(navController) }
        composable(AppScreen.Adb.route) { AdbCompanionScreen(navController) }
        composable(AppScreen.AdbGuide.route) { AdbActivationGuide(navController) }
        composable(AppScreen.Instructions.route) { InstructionsScreen() }
        composable(AppScreen.CommandLogger.route) { CommandLoggerScreen() }
        composable(AppScreen.ProfileManagement.route) { ProfileManagementScreen() }
        composable(AppScreen.OptimizationWizard.route) { OptimizationWizardScreen() }
        composable(
            AppScreen.AppControl.route,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            AppControlScreen(
                navController = navController,
                packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            )
        }

        composable(AppScreen.Home.route) { HomeScreen(navController) }
        composable(AppScreen.Settings.route) { SettingsScreen(navController) }
        composable(
            "${AppScreen.AppManager.route}/{profile}",
            arguments = listOf(navArgument("profile") { type = NavType.StringType })
        ) { backStackEntry ->
            AppManagerScreen(
                navController = navController,
                profile = backStackEntry.arguments?.getString("profile")
            )
        }
        composable(AppScreen.BackupHistory.route) {
            val context = LocalContext.current
            val repository = BackupHistoryRepository(context)
            val factory = BackupHistoryViewModelFactory(repository)
            BackupHistoryScreen(viewModel = viewModel(factory = factory))
        }
    }
}
