
package com.aurorabridge.optimizer.utils

/**
 * A utility class to analyze ADB output and detect various system configurations and limiters.
 */
class AdbAnalyzer(private val adbCommander: IAdbCommander) {

    /**
     * Checks if Aggressive Doze mode is enabled on the device.
     * This method looks for non-default values in the device_idle_constants.
     * The presence of any value is treated as a potential limiter.
     */
    suspend fun isAggressiveDozeEnabled(): AdbCommandResult {
        return adbCommander.runAdbCommandAsync("settings get global device_idle_constants")
    }

    /**
     * Checks if the App Hibernation feature (for unused apps) is enabled.
     * This feature automatically hibernates apps that haven't been used for a while.
     * It is a standard feature on Android 12 and higher.
     */
    suspend fun isAppHibernationEnabled(): AdbCommandResult {
        return adbCommander.runAdbCommandAsync("settings get global unused_apps_hibernate_enabled")
    }
}
