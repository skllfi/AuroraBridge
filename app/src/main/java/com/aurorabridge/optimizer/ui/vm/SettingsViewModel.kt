package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.optimizer.BrandAutoOptimizer
import com.aurorabridge.optimizer.repository.SettingsRepository
import com.aurorabridge.optimizer.utils.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val brandAutoOptimizer: BrandAutoOptimizer,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog = _showConfirmationDialog.asStateFlow()

    val snackbarMessage = MutableSharedFlow<String>()

    fun loadInitialState(context: Context) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loaded(
                profile = brandAutoOptimizer.getProfileForCurrentDevice(context),
                hasBackup = backupManager.hasBackup(context),
                autoOptimizeOnStartup = settingsRepository.isAutoOptimizeOnStartupEnabled(),
                safeModeEnabled = settingsRepository.isSafeModeEnabled(),
                newFeaturesEnabled = settingsRepository.isNewFeaturesEnabled()
            )
        }
    }

    fun onRunOptimizationClicked() {
        _showConfirmationDialog.value = true
    }

    fun onConfirmOptimization(context: Context) {
        _showConfirmationDialog.value = false
        viewModelScope.launch {
            val state = _uiState.value
            if (state is SettingsUiState.Loaded) {
                state.profile?.let {
                    val success = brandAutoOptimizer.applyOptimization(context, it)
                    if (success) {
                        snackbarMessage.emit("Optimization applied successfully!")
                    } else {
                        snackbarMessage.emit("Optimization failed.")
                    }
                }
            }
        }
    }

    fun onDismissDialog() {
        _showConfirmationDialog.value = false
    }

    fun createBackup(context: Context) {
        viewModelScope.launch {
            val success = backupManager.createBackup(context)
            if (success) {
                snackbarMessage.emit("Backup created successfully!")
                loadInitialState(context) // Refresh state
            } else {
                snackbarMessage.emit("Failed to create backup.")
            }
        }
    }

    fun restoreBackup(context: Context) {
        viewModelScope.launch {
            val success = backupManager.restoreBackup(context)
            if (success) {
                snackbarMessage.emit("Backup restored successfully!")
            } else {
                snackbarMessage.emit("Failed to restore backup.")
            }
        }
    }

    fun exportSettings(context: Context) {
        viewModelScope.launch {
            val success = backupManager.exportSettings(context)
            if (success) {
                snackbarMessage.emit("Settings exported successfully!")
            } else {
                snackbarMessage.emit("Failed to export settings.")
            }
        }
    }

    fun importSettings(context: Context, file: File) {
        viewModelScope.launch {
            val success = backupManager.importSettings(context, file)
            if (success) {
                snackbarMessage.emit("Settings imported successfully!")
                loadInitialState(context) // Refresh state
            } else {
                snackbarMessage.emit("Failed to import settings.")
            }
        }
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
