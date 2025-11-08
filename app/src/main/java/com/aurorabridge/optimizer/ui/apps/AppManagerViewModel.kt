package com.aurorabridge.optimizer.ui.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppManagerUiState(
    val apps: List<AppInfo> = emptyList(),
    val selectedApps: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val output: String = "",
    val currentFilter: AppFilter = AppFilter.ALL
)

class AppManagerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppManagerUiState())
    val uiState: StateFlow<AppManagerUiState> = _uiState.asStateFlow()

    private lateinit var appManager: AppManager

    fun loadApps(context: Context) {
        appManager = AppManager(context)
        viewModelScope.launch {
            val apps = appManager.getInstalledApps(_uiState.value.currentFilter)
            _uiState.value = AppManagerUiState(apps = apps, isLoading = false)
        }
    }

    fun setFilter(filter: AppFilter) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, currentFilter = filter, apps = emptyList())
            val apps = appManager.getInstalledApps(filter)
            _uiState.value = _uiState.value.copy(apps = apps, isLoading = false)
        }
    }

    fun toggleAppSelection(packageName: String) {
        val currentSelection = _uiState.value.selectedApps.toMutableSet()
        if (currentSelection.contains(packageName)) {
            currentSelection.remove(packageName)
        } else {
            currentSelection.add(packageName)
        }
        _uiState.value = _uiState.value.copy(selectedApps = currentSelection)
    }

    fun uninstallSelectedApps() {
        viewModelScope.launch {
            val result = appManager.uninstallApps(_uiState.value.selectedApps.toList())
            _uiState.value = _uiState.value.copy(output = result, selectedApps = emptySet())
            loadApps(appManager.context) // Reload apps after uninstall
        }
    }

    fun disableSelectedApps() {
        viewModelScope.launch {
            val result = appManager.disableApps(_uiState.value.selectedApps.toList())
            _uiState.value = _uiState.value.copy(output = result, selectedApps = emptySet())
        }
    }

    fun enableSelectedApps() {
        viewModelScope.launch {
            val result = appManager.enableApps(_uiState.value.selectedApps.toList())
            _uiState.value = _uiState.value.copy(output = result, selectedApps = emptySet())
        }
    }
}
