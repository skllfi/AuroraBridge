package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.repository.AppRepository
import com.aurorabridge.optimizer.ui.apps.AppFilter
import com.aurorabridge.optimizer.ui.apps.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppManagerUiState(
    val apps: List<AppInfo> = emptyList(),
    val selectedApps: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val output: String = "",
    val currentFilter: AppFilter = AppFilter.ALL
)

@HiltViewModel
class AppManagerViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AppManagerUiState())
    val uiState: StateFlow<AppManagerUiState> = _uiState.asStateFlow()

    fun loadApps() {
        viewModelScope.launch {
            val apps = appRepository.getInstalledApps(_uiState.value.currentFilter)
            _uiState.value = AppManagerUiState(apps = apps, isLoading = false)
        }
    }

    fun setFilter(filter: AppFilter) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, currentFilter = filter, apps = emptyList())
            val apps = appRepository.getInstalledApps(filter)
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
            val result = appRepository.uninstallApps(_uiState.value.selectedApps.toList())
            _uiState.value = _uiState.value.copy(output = result, selectedApps = emptySet())
            loadApps() // Reload apps after uninstall
        }
    }

    fun disableSelectedApps() {
        viewModelScope.launch {
            val result = appRepository.disableApps(_uiState.value.selectedApps.toList())
            _uiState.value = _uiState.value.copy(output = result, selectedApps = emptySet())
        }
    }

    fun enableSelectedApps() {
        viewModelScope.launch {
            val result = appRepository.enableApps(_uiState.value.selectedApps.toList())
            _uiState.value = _uiState.value.copy(output = result, selectedApps = emptySet())
        }
    }
}
