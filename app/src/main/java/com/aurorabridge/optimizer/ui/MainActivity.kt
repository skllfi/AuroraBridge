package com.aurorabridge.optimizer.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.optimizer.BrandAutoOptimizer
import com.aurorabridge.optimizer.repository.SettingsRepository
import com.aurorabridge.optimizer.ui.theme.AuroraTheme
import com.aurorabridge.optimizer.utils.LocaleManager
import com.aurorabridge.optimizer.utils.OptimizationNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository
    @Inject
    lateinit var brandAutoOptimizer: BrandAutoOptimizer

    private val localeManager by lazy { LocaleManager() }
    private val notificationHelper by lazy { OptimizationNotificationHelper(this) }
    private val bottomNavItems = listOf(
        Triple(AppScreen.Home, R.drawable.ic_home, R.string.home_title),
        Triple(AppScreen.AppManager, R.drawable.ic_apps, R.string.app_manager_title),
        Triple(AppScreen.Settings, R.drawable.ic_settings, R.string.settings_title)
    )
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationHelper.createNotificationChannel()

        val language = localeManager.getLanguage(this)
        localeManager.setLocale(this, language)

        setContent {
            AuroraTheme {
                navController = rememberNavController()

                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        navController.navigate(AppScreen.UserWarning.route)
                    }
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            bottomNavItems.forEach { (screen, iconRes, titleRes) ->
                                NavigationBarItem(
                                    icon = { Icon(painterResource(id = iconRes), contentDescription = null) },
                                    label = { Text(stringResource(titleRes)) },
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
                    AppNavigation(
                        navController = navController,
                        startDestination = if (settingsRepository.isOnboardingComplete()) AppScreen.UserWarning.route else AppScreen.Onboarding.route,
                        modifier = Modifier.padding(innerPadding),
                        requestPermission = { requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                    )
                }
            }
        }

        runAutoOptimization()
    }

    private fun runAutoOptimization() {
        if (settingsRepository.isAutoOptimizeOnStartupEnabled()) {
            brandAutoOptimizer.getProfileForCurrentDevice(this)?.let { profile ->
                lifecycleScope.launch {
                    val allSucceeded = brandAutoOptimizer.applyOptimization(this@MainActivity, profile)
                    notificationHelper.showAutoOptimizationNotification(allSucceeded)
                }
            }
        }
    }
}
