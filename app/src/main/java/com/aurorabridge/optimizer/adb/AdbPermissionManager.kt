package com.aurorabridge.optimizer.adb

import com.aurorabridge.optimizer.utils.AdbCommander

suspend fun setBatteryOptimization(packageName: String, disabled: Boolean, force: Boolean = false) {
    val command = if (disabled) {
        "dumpsys deviceidle whitelist +$packageName"
    } else {
        "dumpsys deviceidle whitelist -$packageName"
    }
    AdbCommander.runAdbCommandAsync(command, force)
}
