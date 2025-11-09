package com.aurorabridge.optimizer.helper

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdbHelper @Inject constructor() {

    // This is a placeholder for the actual command execution logic.
    // In a real app, this would interact with a shell to run ADB commands.
    private fun executeAdbCommand(command: String): Boolean {
        println("Executing ADB Command: $command")
        // Simulate success for now
        return true
    }

    fun uninstallApp(packageName: String): Boolean {
        // The "-k" option keeps the data and cache directories.
        // For a full uninstall, you would use "pm uninstall $packageName"
        return executeAdbCommand("pm uninstall -k $packageName")
    }

    fun disableApp(packageName: String): Boolean {
        return executeAdbCommand("pm disable-user --user 0 $packageName")
    }

    fun clearCache(packageName: String): Boolean {
        return executeAdbCommand("pm clear $packageName")
    }
}
