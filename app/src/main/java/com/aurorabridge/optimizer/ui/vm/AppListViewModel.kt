package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.adb.AdbCommander
import com.aurorabridge.optimizer.adb.AdbPermissionManager
import com.aurorabridge.optimizer.adb.AdbProfileManager
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

class AppListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

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
    fun loadApps(packageManager: PackageManager) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                val appList = packages
                    // Filter out system apps to focus on user-installed apps
                    .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                    .map {
                        AppInfo(
                            name = it.loadLabel(packageManager).toString(),
                            packageName = it.packageName
                        )
                    }
                    .sortedBy { it.name.lowercase() }

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
                    selectedApps.forEach { app ->
                        AdbPermissionManager.setBatteryOptimization(app.packageName, true)
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
                    selectedApps.forEach { app ->
                        AdbCommander.uninstallApp(app.packageName)
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
                    selectedApps.forEach { app ->
                        AdbProfileManager.applyProfile(context, app.packageName, profileName)
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
}

// Sealed interface to represent the UI state
sealed interface AppListUiState {
    object Loading : AppListUiState
    data class Success(
        val apps: List<AppInfo>,
        val recommendedProfile: String? = null
    ) : AppListUiState
    data class Error(val message: String) : AppListUiState
}
