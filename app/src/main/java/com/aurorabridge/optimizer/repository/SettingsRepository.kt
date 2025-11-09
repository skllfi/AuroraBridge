package com.aurorabridge.optimizer.repository

import com.aurorabridge.optimizer.utils.SettingsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val settingsManager: SettingsManager) {

    fun isOnboardingComplete(): Boolean {
        return settingsManager.isOnboardingComplete()
    }

    fun setOnboardingComplete(complete: Boolean) {
        settingsManager.setOnboardingComplete(complete)
    }

    fun isAutoOptimizeOnStartupEnabled(): Boolean {
        return settingsManager.isAutoOptimizeOnStartupEnabled()
    }

    fun setAutoOptimizeOnStartup(enabled: Boolean) {
        settingsManager.setAutoOptimizeOnStartup(enabled)
    }

    fun isSafeModeEnabled(): Boolean {
        return settingsManager.isSafeModeEnabled()
    }

    fun setSafeMode(enabled: Boolean) {
        settingsManager.setSafeMode(enabled)
    }

    fun isNewFeaturesEnabled(): Boolean {
        return settingsManager.isNewFeaturesEnabled()
    }

    fun setNewFeaturesEnabled(enabled: Boolean) {
        settingsManager.setNewFeaturesEnabled(enabled)
    }
}
