package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.optimizer.BrandAutoOptimizer
import com.aurorabridge.optimizer.repository.SettingsRepository
import com.aurorabridge.optimizer.utils.CommandParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val brandAutoOptimizer: BrandAutoOptimizer
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog = _showConfirmationDialog.asStateFlow()

    val snackbarMessage = MutableSharedFlow<String>()

    fun loadInitialState() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loaded(
                profile = brandAutoOptimizer.getProfileForCurrentDevice(),
                hasBackup = false, // Deprecated: No longer managed here
                autoOptimizeOnStartup = settingsRepository.isAutoOptimizeOnStartupEnabled(),
                safeModeEnabled = settingsRepository.isSafeModeEnabled(),
                newFeaturesEnabled = settingsRepository.isNewFeaturesEnabled()
            )
        }
    }

    fun onRunOptimizationClicked() {
        val state = _uiState.value
        if (state is SettingsUiState.Loaded && state.profile != null) {
            val parsed = CommandParser.parse(state.profile.commands)
            _uiState.update { (it as SettingsUiState.Loaded).copy(parsedCommands = parsed) }
            _showConfirmationDialog.value = true
        }
    }

    fun onConfirmOptimization() {
        _showConfirmationDialog.value = false
        viewModelScope.launch {
            val state = _uiState.value
            if (state is SettingsUiState.Loaded && state.profile != null) {
                val results = brandAutoOptimizer.applyOptimization(state.profile.brandName)
                val successCount = results.values.count { !it.contains("error", ignoreCase = true) }
                val totalCount = results.size

                if (successCount == totalCount) {
                    snackbarMessage.emit("Optimization applied successfully!")
                } else {
                    snackbarMessage.emit("Optimization partially applied ($successCount/$totalCount). Check logs for details.")
                }
            }
        }
    }

    fun onDismissDialog() {
        _showConfirmationDialog.value = false
    }

    fun onAutoOptimizeOnStartupChanged(enabled: Boolean) {
        settingsRepository.setAutoOptimizeOnStartup(enabled)
        val state = _uiState.value
        if (state is SettingsUiState.Loaded) {
            _uiState.value = state.copy(autoOptimizeOnStartup = enabled)
        }
    }

    fun onSafeModeChanged(enabled: Boolean) {
        settingsRepository.setSafeMode(enabled)
        val state = _uiState.value
        if (state is SettingsUiState.Loaded) {
            _uiState.value = state.copy(safeModeEnabled = enabled)
        }
    }

    fun onNewFeaturesEnabledChanged(enabled: Boolean) {
        settingsRepository.setNewFeaturesEnabled(enabled)
        val state = _uiState.value
        if (state is SettingsUiState.Loaded) {
            _uiState.value = state.copy(newFeaturesEnabled = enabled)
        }
    }
}
