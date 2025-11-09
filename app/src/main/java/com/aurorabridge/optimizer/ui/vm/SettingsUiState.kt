package com.aurorabridge.optimizer.ui.vm

import com.aurorabridge.optimizer.optimizer.OptimizationProfile
import com.aurorabridge.optimizer.utils.ParsedCommand

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Loaded(
        val profile: OptimizationProfile?,
        val hasBackup: Boolean, // This might be deprecated soon
        val autoOptimizeOnStartup: Boolean,
        val safeModeEnabled: Boolean,
        val newFeaturesEnabled: Boolean,
        val parsedCommands: List<ParsedCommand> = emptyList()
    ) : SettingsUiState()
}
