package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.adb.AdbProfileManager
import com.aurorabridge.optimizer.adb.setBatteryOptimization
import com.aurorabridge.optimizer.utils.AdbCommander
import com.aurorabridge.optimizer.utils.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data class to hold application information
data class AppInfo(
    val name: String,
    val packageName: String,
    val isSelected: Boolean = false
)

class AppListViewModel(savedStateHandle: SavedStateHandle, private val settingsManager: SettingsManager) : ViewModel() {

    private val recommendedProfile: String? = savedStateHandle.get<String>("profile")

    private val _uiState = MutableStateFlow<AppListUiState>(AppListUiState.Loading)
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isSelectionModeActive = MutableStateFlow(false)
    val isSelectionModeActive = _isSelectionModeActive.asStateFlow()

    fun enterSelectionMode() {
        _isSelectionModeActive.value = true
    }

    fun exitSelectionMode() {
        _isSelectionModeActive.value = false
        clearSelections()
    }

    private fun clearSelections() {
        _uiState.update {
            if (it is AppListUiState.Success) {
                it.copy(apps = it.apps.map { app -> app.copy(isSelected = false) })
            } else {
                it
            }
        }
    }

    // Function to load non-system applications
    fun loadApps(packageManager: PackageManager, context: Context, profile: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                var appList = packages
                    // Filter out system apps to focus on user-installed apps
                    .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                    .map {
                        AppInfo(
                            name = it.loadLabel(packageManager).toString(),
                            packageName = it.packageName
                        )
                    }
                    .sortedBy { it.name.lowercase() }

                if (profile != null && profile != "default") {
                    appList = appList.filter { AdbProfileManager.getProfileForApp(context, it.packageName) == profile }
                }

                // Post the successful result to the UI state
                _uiState.value = AppListUiState.Success(
                    apps = appList,
                    recommendedProfile = if (recommendedProfile != "default") recommendedProfile else null
                )
            } catch (e: Exception) {
                // Post the error to the UI state
                _uiState.value = AppListUiState.Error(e.message ?: "An unknown error occurred while loading apps")
            }
        }
    }

    fun toggleAppSelection(appInfo: AppInfo) {
        val currentState = _uiState.value
        if (currentState is AppListUiState.Success) {
            val updatedApps = currentState.apps.map {
                if (it.packageName == appInfo.packageName) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
            _uiState.value = currentState.copy(apps = updatedApps)
        }
    }

    fun activateSelectionAndToggle(appInfo: AppInfo) {
        enterSelectionMode()
        toggleAppSelection(appInfo)
    }

    fun disableBatteryOptimizationForSelectedApps() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppListUiState.Success) {
                val selectedApps = currentState.apps.filter { it.isSelected }
                if (selectedApps.isNotEmpty()) {
                    val commands = selectedApps.map { "dumpsys deviceidle whitelist +${it.packageName}" }

                    if (settingsManager.isSafeModeEnabled()) {
                        _uiState.value = AppListUiState.ConfirmAction(
                            commands = commands,
                            action = { forceDisableBatteryOptimizationForSelectedApps(force = true) }
                        )
                    } else {
                        forceDisableBatteryOptimizationForSelectedApps(force = false)
                    }
                }
            }
        }
    }

    private fun forceDisableBatteryOptimizationForSelectedApps(force: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppListUiState.Success) {
                val selectedApps = currentState.apps.filter { it.isSelected }
                if (selectedApps.isNotEmpty()) {
                    selectedApps.forEach { app ->
                        setBatteryOptimization(app.packageName, true, force)
                    }
                    _snackbarMessage.value = "Battery optimization disabled for selected apps"
                    exitSelectionMode()
                }
            }
        }
    }

    fun uninstallSelectedApps() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppListUiState.Success) {
                val selectedApps = currentState.apps.filter { it.isSelected }
                if (selectedApps.isNotEmpty()) {
                    val commands = selectedApps.map { "pm uninstall -k --user 0 ${it.packageName}" }

                    if (settingsManager.isSafeModeEnabled()) {
                        _uiState.value = AppListUiState.ConfirmAction(
                            commands = commands,
                            action = { forceUninstallSelectedApps(force = true) }
                        )
                    } else {
                        forceUninstallSelectedApps(force = false)
                    }
                }
            }
        }
    }

    private fun forceUninstallSelectedApps(force: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppListUiState.Success) {
                val selectedApps = currentState.apps.filter { it.isSelected }
                if (selectedApps.isNotEmpty()) {
                    selectedApps.forEach { app ->
                        AdbCommander.uninstallApp(app.packageName, force)
                    }
                    _snackbarMessage.value = "Selected apps uninstalled"
                    exitSelectionMode()
                }
            }
        }
    }

    fun applyFixProfileForSelectedApps(context: Context, profileName: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppListUiState.Success) {
                val selectedApps = currentState.apps.filter { it.isSelected }
                if (selectedApps.isNotEmpty()) {
                    val commands = AdbProfileManager.getCommandsForProfile(profileName)

                    if (settingsManager.isSafeModeEnabled()) {
                        _uiState.value = AppListUiState.ConfirmAction(
                            commands = commands,
                            action = { forceApplyFixProfileForSelectedApps(context, profileName, force = true) }
                        )
                    } else {
                        forceApplyFixProfileForSelectedApps(context, profileName, force = false)
                    }
                }
            }
        }
    }

    private fun forceApplyFixProfileForSelectedApps(context: Context, profileName: String, force: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppListUiState.Success) {
                val selectedApps = currentState.apps.filter { it.isSelected }
                if (selectedApps.isNotEmpty()) {
                    selectedApps.forEach { app ->
                        AdbProfileManager.applyProfile(context, app.packageName, profileName, force)
                    }
                    _snackbarMessage.value = "Applied profile '$profileName' to selected apps."
                    exitSelectionMode()
                }
            }
        }
    }

    fun getAvailableProfiles(): List<String> {
        return AdbProfileManager.getAvailableProfiles()
    }

    fun onSnackbarShown() {
        _snackbarMessage.value = null
    }

    fun onConfirmationDialogDismissed() {
        // Reset the UI state to dismiss the dialog
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppListUiState.ConfirmAction) {
                _uiState.value = AppListUiState.Success((_uiState.value as AppListUiState.Success).apps)
            }
        }
    }
}

// Sealed interface to represent the UI state
sealed interface AppListUiState {
    object Loading : AppListUiState
    data class Success(
        val apps: List<AppInfo>,
        val recommendedProfile: String? = null
    ) : AppListUiState

    data class ConfirmAction(
        val commands: List<String>,
        val action: () -> Unit
    ) : AppListUiState

    data class Error(val message: String) : AppListUiState
}
