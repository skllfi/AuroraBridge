package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.helper.AdbHelper
import com.aurorabridge.optimizer.model.AppCategory
import com.aurorabridge.optimizer.model.AppItem
import com.aurorabridge.optimizer.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AppManagerUiState {
    object Loading : AppManagerUiState()
    data class Success(
        val apps: List<AppItem>,
        val allApps: List<AppItem> = emptyList(),
        val activeFilters: Set<AppCategory> = AppCategory.values().toSet(),
        val message: String? = null,
        val isSelectionModeActive: Boolean = false,
        val selectedApps: Set<String> = emptySet()
    ) : AppManagerUiState()
    data class Error(val message: String) : AppManagerUiState()
}

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val adbHelper: AdbHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppManagerUiState>(AppManagerUiState.Loading)
    val uiState: StateFlow<AppManagerUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _uiState.value = AppManagerUiState.Loading
            try {
                val allApps = appRepository.getInstalledApps()
                _uiState.value = AppManagerUiState.Success(apps = allApps, allApps = allApps)
            } catch (e: Exception) {
                _uiState.value = AppManagerUiState.Error("Failed to load apps: ${e.message}")
            }
        }
    }

    fun toggleFilter(category: AppCategory) {
        val currentState = _uiState.value
        if (currentState is AppManagerUiState.Success) {
            val newFilters = currentState.activeFilters.toMutableSet()
            if (newFilters.contains(category)) {
                newFilters.remove(category)
            } else {
                newFilters.add(category)
            }
            val filteredApps = currentState.allApps.filter { it.category in newFilters }
            _uiState.value = currentState.copy(apps = filteredApps, activeFilters = newFilters)
        }
    }

    fun uninstallApp(packageName: String) {
        performActionAndReload("Uninstalled", "uninstall", adbHelper::uninstallApp, packageName)
    }

    fun disableApp(packageName: String) {
        performActionAndReload("Disabled", "disable", adbHelper::disableApp, packageName)
    }

    fun clearCache(packageName: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppManagerUiState.Success) {
                val result = adbHelper.clearCache(packageName)
                val message = if (result) "Cache cleared for $packageName" else "Failed to clear cache for $packageName"
                _uiState.value = currentState.copy(message = message)
            }
        }
    }

    private fun performActionAndReload(
        actionPastTense: String,
        actionPresentTense: String,
        action: suspend (String) -> Boolean,
        packageName: String
    ) {
        viewModelScope.launch {
            val result = action(packageName)
            val message = if (result) "$actionPastTense $packageName" else "Failed to $actionPresentTense $packageName"
            reloadAppsWithMessage(message)
        }
    }
    
    private fun reloadAppsWithMessage(message: String) {
        val currentUiState = _uiState.value
        if(currentUiState is AppManagerUiState.Success){
            viewModelScope.launch {
                 try {
                    val allApps = appRepository.getInstalledApps()
                    val filteredApps = allApps.filter { it.category in currentUiState.activeFilters }
                    _uiState.value = currentUiState.copy(apps = filteredApps, allApps = allApps, message = message)
                } catch (e: Exception) {
                    _uiState.value = AppManagerUiState.Error("Failed to reload apps: ${e.message}")
                }
            }
        }
    }

    fun messageShown() {
        val currentState = _uiState.value
        if (currentState is AppManagerUiState.Success && currentState.message != null) {
            _uiState.update { currentState.copy(message = null) }
        }
    }

    // --- Batch Operations ---

    fun enterSelectionMode() {
        val currentState = _uiState.value
        if (currentState is AppManagerUiState.Success) {
            _uiState.update { currentState.copy(isSelectionModeActive = true) }
        }
    }

    fun exitSelectionMode() {
        val currentState = _uiState.value
        if (currentState is AppManagerUiState.Success) {
            _uiState.update { currentState.copy(isSelectionModeActive = false, selectedApps = emptySet()) }
        }
    }

    fun toggleAppSelection(packageName: String) {
        val currentState = _uiState.value
        if (currentState is AppManagerUiState.Success) {
            val newSelectedApps = currentState.selectedApps.toMutableSet()
            if (newSelectedApps.contains(packageName)) {
                newSelectedApps.remove(packageName)
            } else {
                newSelectedApps.add(packageName)
            }

            _uiState.update { 
                val updatedState = currentState.copy(selectedApps = newSelectedApps)
                if(updatedState.selectedApps.isEmpty()){
                    updatedState.copy(isSelectionModeActive = false)
                } else {
                    updatedState.copy(isSelectionModeActive = true)
                }
            }
        }
    }

    fun uninstallSelectedApps() {
        val currentState = _uiState.value
        if (currentState is AppManagerUiState.Success) {
            performBatchActionAndReload("Uninstalled", "uninstall", adbHelper::uninstallApp, currentState.selectedApps)
        }
    }

    fun disableSelectedApps() {
        val currentState = _uiState.value
        if (currentState is AppManagerUiState.Success) {
            performBatchActionAndReload("Disabled", "disable", adbHelper::disableApp, currentState.selectedApps)
        }
    }

    private fun performBatchActionAndReload(
        actionPastTense: String,
        actionPresentTense: String,
        action: suspend (String) -> Boolean,
        packageNames: Set<String>
    ) {
        viewModelScope.launch {
            if (packageNames.isEmpty()) return@launch

            var successCount = 0
            packageNames.forEach { packageName ->
                if (action(packageName)) {
                    successCount++
                }
            }
            val message = "$actionPastTense $successCount out of ${packageNames.size} apps."
            
            exitSelectionMode()
            reloadAppsWithMessage(message)
        }
    }
}
