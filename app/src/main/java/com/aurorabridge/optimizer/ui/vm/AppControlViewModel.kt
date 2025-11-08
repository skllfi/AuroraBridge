package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.adb.AdbPermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppControlViewModel : ViewModel() {

    private val _isBatteryOptimizationOff = MutableStateFlow(false)
    val isBatteryOptimizationOff: StateFlow<Boolean> = _isBatteryOptimizationOff

    fun getAppName(context: Context, packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    fun checkBatteryOptimizationStatus(context: Context, packageName: String) {
        viewModelScope.launch {
            _isBatteryOptimizationOff.value = AdbPermissionManager.isAppInWhitelist(packageName)
        }
    }

    fun setBatteryOptimization(context: Context, packageName: String, disabled: Boolean) {
        viewModelScope.launch {
            AdbPermissionManager.setBatteryOptimization(context, packageName, disabled)
            _isBatteryOptimizationOff.value = disabled
        }
    }
}
