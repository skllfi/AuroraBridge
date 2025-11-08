package com.aurorabridge.optimizer.utils

import android.os.Build

object DeviceIdentifier {

    fun getDeviceBrand(): String {
        return Build.MANUFACTURER
    }

    fun getDeviceModel(): String {
        return Build.MODEL
    }

    fun getOsVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getRecommendedProfile(): String {
        return when (getDeviceBrand().lowercase()) {
            "huawei" -> "HuaweiFix"
            "xiaomi" -> "XiaomiFix"
            // Add other brands here
            else -> "UniversalFix"
        }
    }
}
