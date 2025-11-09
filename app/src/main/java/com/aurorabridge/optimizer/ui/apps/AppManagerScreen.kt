package com.aurorabridge.optimizer.ui.apps

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.aurorabridge.optimizer.ui.screens.AppListScreen

@Composable
fun AppManagerScreen(navController: NavController, profile: String? = null) {
    AppListScreen(navController = navController, profile = profile)
}
