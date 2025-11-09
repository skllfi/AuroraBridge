package com.aurorabridge.optimizer.ui.vm

import com.aurorabridge.optimizer.optimizer.OptimizationProfile

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Loaded(
        val profile: OptimizationProfile?,
        val hasBackup: Boolean,
        val autoOptimizeOnStartup: Boolean,
        val safeModeEnabled: Boolean,
        val newFeaturesEnabled: Boolean
    ) : SettingsUiState()
}
