
package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.Settings
import com.aurorabridge.optimizer.optimizer.BrandAutoOptimizer
import com.aurorabridge.optimizer.optimizer.OptimizationProfile
import com.aurorabridge.optimizer.utils.BackupManager
import com.aurorabridge.optimizer.utils.SettingsManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private lateinit var settingsManager: SettingsManager
    private lateinit var backupManager: BackupManager

    fun loadInitialState(context: Context) {
        settingsManager = SettingsManager(context)
        backupManager = BackupManager(context)
        viewModelScope.launch {
            val profile = BrandAutoOptimizer.getProfileForCurrentDevice(context)
            val hasBackup = BackupManager.hasBackup(context)
            val autoOptimizeEnabled = settingsManager.isAutoOptimizeOnStartupEnabled()
            val safeModeEnabled = settingsManager.isSafeModeEnabled()
            val newFeaturesEnabled = settingsManager.isNewFeaturesEnabled()
            _uiState.value = SettingsUiState.Loaded(
                profile = profile,
                hasBackup = hasBackup,
                autoOptimizeOnStartup = autoOptimizeEnabled,
                safeModeEnabled = safeModeEnabled,
                newFeaturesEnabled = newFeaturesEnabled
            )
        }
    }

    fun onRunOptimizationClicked() {
        _showConfirmationDialog.value = true
    }

    fun onConfirmOptimization(context: Context) {
        viewModelScope.launch {
            (_uiState.value as? SettingsUiState.Loaded)?.let {
                it.profile?.let { profile ->
                    BrandAutoOptimizer.applyOptimization(context, profile)
                    _snackbarMessage.emit(context.getString(R.string.auto_optimization_notification_success))
                }
            }
            _showConfirmationDialog.value = false
        }
    }

    fun onDismissDialog() {
        _showConfirmationDialog.value = false
    }

    fun createBackup(context: Context) {
        viewModelScope.launch {
            (_uiState.value as? SettingsUiState.Loaded)?.profile?.let {
                val success = BackupManager.createBackup(context, it)
                if (success) {
                    _snackbarMessage.emit(context.getString(R.string.settings_backup_created))
                    _uiState.update {
                        (it as SettingsUiState.Loaded).copy(hasBackup = true)
                    }
                } else {
                    _snackbarMessage.emit(context.getString(R.string.settings_backup_failed))
                }
            }
        }
    }

    fun restoreBackup(context: Context) {
        viewModelScope.launch {
            val success = BackupManager.restoreBackup(context)
            if (success) {
                _snackbarMessage.emit(context.getString(R.string.settings_restore_success))
            } else {
                _snackbarMessage.emit(context.getString(R.string.settings_restore_failed))
            }
        }
    }

    fun onAutoOptimizeOnStartupChanged(enabled: Boolean) {
        settingsManager.setAutoOptimizeOnStartup(enabled)
        _uiState.update {
            (it as SettingsUiState.Loaded).copy(autoOptimizeOnStartup = enabled)
        }
    }

    fun onSafeModeChanged(enabled: Boolean) {
        settingsManager.setSafeMode(enabled)
        _uiState.update {
            (it as SettingsUiState.Loaded).copy(safeModeEnabled = enabled)
        }
    }

    fun onNewFeaturesEnabledChanged(enabled: Boolean) {
        settingsManager.setNewFeaturesEnabled(enabled)
        _uiState.update {
            (it as SettingsUiState.Loaded).copy(newFeaturesEnabled = enabled)
        }
    }

    fun exportSettings(context: Context) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? SettingsUiState.Loaded) ?: return@launch
            val settings = Settings(
                experimentalFeaturesEnabled = currentState.newFeaturesEnabled
            )
            val file = backupManager.exportSettings(settings)
            if (file != null) {
                _snackbarMessage.emit("Settings exported to ${file.absolutePath}")
            } else {
                _snackbarMessage.emit("Failed to export settings")
            }
        }
    }

    fun importSettings(context: Context, file: File) {
        viewModelScope.launch {
            val settings = backupManager.importSettings(file)
            if (settings != null) {
                settingsManager.setNewFeaturesEnabled(settings.experimentalFeaturesEnabled)
                _uiState.update {
                    (it as SettingsUiState.Loaded).copy(newFeaturesEnabled = settings.experimentalFeaturesEnabled)
                }
                _snackbarMessage.emit("Settings imported successfully")
            } else {
                _snackbarMessage.emit("Failed to import settings")
            }
        }
    }

    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    }
}

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Loaded(
        val profile: OptimizationProfile?,
        val hasBackup: Boolean,
        val autoOptimizeOnStartup: Boolean,
        val safeModeEnabled: Boolean,
        val newFeaturesEnabled: Boolean
    ) : SettingsUiState
}
