package com.aurorabridge.optimizer.adb

import android.content.Context
import android.util.Log
import com.aurorabridge.optimizer.utils.BackupAndRestoreManager
import java.io.BufferedReader
import java.io.InputStreamReader

object AdbOptimizer {
    private const val TAG = "AdbOptimizer"

    fun applyProfile(context: Context, profileName: String): Map<String, String> {
        val commands = AdbProfiles.getCommandsForProfile(profileName)
        if (commands.isEmpty()) {
            Log.w(TAG, "No commands found for profile: $profileName")
            return mapOf("error" to "Profile not found or is empty.")
        }

        // Create a backup before applying the commands
        val backupSuccess = BackupAndRestoreManager.createBackupFromCommands(context, profileName, commands)
        if (!backupSuccess) {
            Log.e(TAG, "Failed to create a backup for profile: $profileName. Aborting optimization.")
            return mapOf("error" to "Failed to create backup. Aborting.")
        }

        Log.i(TAG, "Backup created successfully for profile: $profileName. Proceeding with commands.")
        return runCommands(commands)
    }

    private fun runCommands(cmds: List<String>): Map<String,String> {
        val results = mutableMapOf<String,String>()
        for (cmd in cmds) {
            try {
                // Using AdbCommander for command execution to unify the logic
                val result = AdbCommander.runShellCommand(cmd)
                results[cmd] = result.getOrDefault("output", "")
            } catch (e: Exception) {
                Log.e(TAG, "cmd failed $cmd", e)
                results[cmd] = "error: $e"
            }
        }
        return results
    }
}
