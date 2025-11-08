package com.aurorabridge.optimizer.ui.vm

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to hold application information
data class AppInfo(
    val name: String,
    val packageName: String,
)

class AppListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AppListUiState>(AppListUiState.Loading)
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

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
                _uiState.value = AppListUiState.Success(appList)
            } catch (e: Exception) {
                // Post the error to the UI state
                _uiState.value = AppListUiState.Error(e.message ?: "An unknown error occurred while loading apps")
            }
        }
    }
}

// Sealed interface to represent the UI state
sealed interface AppListUiState {
    object Loading : AppListUiState
    data class Success(val apps: List<AppInfo>) : AppListUiState
    data class Error(val message: String) : AppListUiState
}