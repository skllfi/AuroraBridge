package com.aurorabridge.optimizer.utils

import android.content.Context

class SettingsManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun isAutoOptimizeOnStartupEnabled(): Boolean {
        return sharedPreferences.getBoolean("auto_optimize_on_startup", false)
    }

    fun setAutoOptimizeOnStartup(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("auto_optimize_on_startup", enabled).apply()
    }

    fun isSafeModeEnabled(): Boolean {
        return sharedPreferences.getBoolean("safe_mode", false)
    }

    fun setSafeMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("safe_mode", enabled).apply()
    }

    fun isNewFeaturesEnabled(): Boolean {
        return sharedPreferences.getBoolean("new_features_enabled", false)
    }

    fun setNewFeaturesEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("new_features_enabled", enabled).apply()
    }
}
