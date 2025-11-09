package com.aurorabridge.optimizer.adb

import android.content.Context
import com.aurorabridge.optimizer.utils.AdbCommander
import com.aurorabridge.optimizer.utils.BackupAndRestoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AdbProfileManager {

    // A map to hold the commands for each profile
    private val profiles: Map<String, List<String>> = mapOf(
        "HuaweiFix" to listOf(
            "settings put global settings_enable_monitor_some_apps 0",
            "settings put global top_n_app_clean 0",
            "settings put system abnormal_app_mem_level 0",
            "settings put system abnormal_app_mem_switch 0"
        ),
        "XiaomiFix" to listOf(
            "settings put global force_fsg_v2 1",
            "settings put global enable_miui_optimization 0"
        ),
        "UniversalFix" to listOf(
            "settings put secure adaptive_connectivity_enabled 0",
            "settings put secure screen_off_timeout 600000", // 10 minutes
            "settings put global wifi_scan_throttle_enabled 0"
        )
    )

    suspend fun applyProfile(
        context: Context,
        packageName: String,
        profileName: String,
        force: Boolean = false
    ) {
        withContext(Dispatchers.IO) {
            val commands = profiles[profileName]
            if (commands != null) {
                // Create a list of settings keys that will be changed
                val settingsToBackup = commands.mapNotNull { command ->
                    // We are only backing up `settings put` commands
                    if (command.startsWith("settings put")) {
                        val parts = command.split(" ")
                        // Format: "namespace key"
                        if (parts.size >= 4) "${parts[2]} ${parts[3]}" else null
                    } else {
                        null
                    }
                }

                // Create and save a snapshot before applying changes
                val snapshot = BackupAndRestoreManager.createSettingsSnapshot(settingsToBackup)
                BackupAndRestoreManager.saveBackupToFile(context, profileName, packageName, snapshot)

                // Now, apply the new settings
                AdbCommander.runAdbCommandsAsync(commands, force)
            }
        }
    }

    fun getProfileForApp(context: Context, packageName: String): String? {
        val backupDir = context.filesDir
        val backupFiles = backupDir.listFiles { _, name -> name.startsWith("optimizer_backup_${packageName}_") && name.endsWith(".json") }

        backupFiles?.firstOrNull()?.let { file ->
            val backupData = BackupAndRestoreManager.readBackupFromFile(file)
            return backupData?.profileName
        }

        return null
    }

    fun getAvailableProfiles(): List<String> {
        return profiles.keys.toList()
    }

    fun getCommandsForProfile(profileName: String): List<String> {
        return profiles[profileName] ?: emptyList()
    }
}
