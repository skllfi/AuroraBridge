package com.aurorabridge.optimizer.ui

sealed class AppScreen(val route: String) {
    object Onboarding : AppScreen("onboarding")
    object Permissions : AppScreen("permissions")
    object UserWarning : AppScreen("user_warning")
    object Diagnostics : AppScreen("diagnostics")
    object Adb : AppScreen("adb")
    object AdbGuide : AppScreen("adb_guide")
    object Instructions : AppScreen("instructions")
    object CommandLogger : AppScreen("command_logger")
    object ProfileManagement : AppScreen("profile_management")
    object AppControl : AppScreen("app_control_screen/{packageName}") {
        fun createRoute(packageName: String) = "app_control_screen/$packageName"
    }
    object Home : AppScreen("home")
    object Settings : AppScreen("settings")
    object AppManager : AppScreen("app_manager")
    object BackupHistory : AppScreen("backup_history")
    object OptimizationWizard : AppScreen("optimization_wizard")
}
