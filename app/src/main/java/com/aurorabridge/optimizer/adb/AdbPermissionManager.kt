package com.aurorabridge.optimizer.adb

import android.content.Context
import android.os.PowerManager
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object AdbPermissionManager {
    private const val TAG = "AdbPermissionManager"

    fun setBatteryOptimization(context: Context, packageName: String, disabled: Boolean) {
        val command = if (disabled) {
            "dumpsys deviceidle whitelist +$packageName"
        } else {
            "dumpsys deviceidle whitelist -$packageName"
        }

        try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                Log.e(TAG, "Command '$command' failed with exit code $exitCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while running command '$command'", e)
        }
    }

    fun isBatteryOptimizationDisabled(context: Context, packageName: String): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    // Check whitelist with shell command
    fun isAppInWhitelist(packageName: String): Boolean {
        try {
            val process = Runtime.getRuntime().exec("dumpsys deviceidle whitelist")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line?.contains(packageName) == true) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check whitelist", e)
        }
        return false
    }
}
