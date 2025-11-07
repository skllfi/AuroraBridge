package com.aurorabridge.optimizer.adb

object AdbProfiles {
    // Profiles: mapping to lists of shell commands (strings) to run via ADB Companion
    val HuaweiFix = listOf(
        "pm disable-user com.huawei.powergenie || true",
        "pm disable-user com.huawei.hybridservice || true",
        "dumpsys deviceidle whitelist +com.aurorabridge.optimizer || true"
    )
    val XiaomiFix = listOf(
        "settings put global miui_optimization 0 || true",
        "pm grant com.aurorabridge.optimizer android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS || true"
    )
    val UniversalFix = listOf(
        "settings put global app_standby_enabled 0 || true",
        "dumpsys deviceidle disable || true"
    )
}
