package com.aurorabridge.optimizer.viewmodel

import androidx.lifecycle.ViewModel
import com.aurorabridge.optimizer.adb.AdbConnectionManager
import com.aurorabridge.optimizer.adb.AdbOptimizer
import com.aurorabridge.optimizer.adb.AdbProfiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdbViewModel : ViewModel() {
    private val _status = MutableStateFlow("idle")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _showConfirmDialog = MutableStateFlow<String?>(null)
    val showConfirmDialog: StateFlow<String?> = _showConfirmDialog.asStateFlow()

    fun enableAdbWifi() {
        CoroutineScope(Dispatchers.IO).launch {
            val ok = AdbConnectionManager.enableAdbWifi()
            _status.emit(if (ok) "enabled" else "failed")
        }
    }

    fun runProfile(name: String) {
        // Don't run directly, show dialog first
        _showConfirmDialog.value = name
    }

    fun confirmRunProfile(name: String) {
        _showConfirmDialog.value = null // Hide dialog
        CoroutineScope(Dispatchers.IO).launch {
            val results = when(name) {
                "huawei" -> AdbOptimizer.runCommands(AdbProfiles.HuaweiFix)
                "xiaomi" -> AdbOptimizer.runCommands(AdbProfiles.XiaomiFix)
                else -> AdbOptimizer.runCommands(AdbProfiles.UniversalFix)
            }
            // just update status for now
            _status.emit("profile_run:${name}")
        }
    }

    fun dismissDialog() {
        _showConfirmDialog.value = null
    }
}
