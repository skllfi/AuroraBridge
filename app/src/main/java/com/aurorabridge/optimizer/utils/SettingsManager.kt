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
}
