package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aurorabridge.optimizer.services.AppMonitorWorker
import com.aurorabridge.optimizer.utils.LogUtils
import com.aurorabridge.optimizer.utils.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsViewModel : ViewModel() {

    private val _autoStartState = MutableStateFlow(false)
    val autoStartState: StateFlow<Boolean> = _autoStartState.asStateFlow()

    private val _backgroundWorkState = MutableStateFlow(false)
    val backgroundWorkState: StateFlow<Boolean> = _backgroundWorkState.asStateFlow()

    private val languageViewModel = LanguageViewModel()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    fun loadSettings(context: Context) {
        val prefs = getPrefs(context)
        _autoStartState.value = prefs.getBoolean("auto_start_enabled", false)
        _backgroundWorkState.value = prefs.getBoolean("background_work_enabled", false)
    }

    fun toggleAutoStart(enabled: Boolean, context: Context) {
        _autoStartState.value = enabled
        getPrefs(context).edit().putBoolean("auto_start_enabled", enabled).apply()
    }

    fun toggleBackgroundWork(enabled: Boolean, context: Context) {
        _backgroundWorkState.value = enabled
        getPrefs(context).edit().putBoolean("background_work_enabled", enabled).apply()

        val workManager = WorkManager.getInstance(context)
        if (enabled) {
            val appMonitorWorkRequest = PeriodicWorkRequestBuilder<AppMonitorWorker>(12, TimeUnit.HOURS)
                .build()
            workManager.enqueueUniquePeriodicWork(
                "app_monitor_work",
                ExistingPeriodicWorkPolicy.REPLACE,
                appMonitorWorkRequest
            )
        } else {
            workManager.cancelUniqueWork("app_monitor_work")
        }
    }

    fun exportLogs(context: Context, brand: String): String {
        val basePath = context.filesDir.absolutePath
        LogUtils.saveReport(basePath, mapOf("brand" to brand))
        return "Saved logs to $basePath"
    }

    fun setLanguage(context: Context, language: String) {
        languageViewModel.setLanguage(context, language)
    }
}
