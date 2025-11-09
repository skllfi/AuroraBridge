
package com.aurorabridge.optimizer.utils

import android.content.Context
import com.aurorabridge.optimizer.adb.AdbCommander
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val timestamp: Long,
    val profileName: String,
    val backedUpSettings: Map<String, String>,
    val uninstalledPackages: List<String>
)

object BackupAndRestoreManager {

    private val json = Json { prettyPrint = true }

    /**
     * Analyzes a list of ADB commands, creates a backup of the settings and package states
     * that will be changed, and saves it to a file.
     *
     * @param context The application context.
     * @param profileName The name of the optimization profile being applied.
     * @param commands The list of ADB commands that will be executed.
     * @return True if the backup was created successfully, false otherwise.
     */
    fun createBackupFromCommands(context: Context, profileName: String, commands: List<String>): Boolean {
        val settingsKeysToBackup = mutableListOf<String>()
        val packagesToBackup = mutableListOf<String>()

        val uninstallRegex = """pm\s+uninstall\s+(-k\s+)?--user\s+\d+\s+([\w\.]+)""".toRegex()
        val settingsPutRegex = """settings\s+put\s+(\w+)\s+([\w\.\/]+)""".toRegex()

        commands.forEach { command ->
            val uninstallMatch = uninstallRegex.find(command)
            val settingsMatch = settingsPutRegex.find(command)

            if (uninstallMatch != null) {
                val packageName = uninstallMatch.groupValues[2]
                packagesToBackup.add(packageName)
            } else if (settingsMatch != null) {
                val namespace = settingsMatch.groupValues[1]
                val key = settingsMatch.groupValues[2]
                settingsKeysToBackup.add("$namespace $key")
            }
        }

        val settingsSnapshot = createSettingsSnapshot(settingsKeysToBackup)

        return saveBackupToFile(context, profileName, settingsSnapshot, packagesToBackup)
    }

    /**
     * Creates a snapshot of current system settings based on a list of settings keys.
     */
    private fun createSettingsSnapshot(settingsKeys: List<String>): Map<String, String> {
        val backupData = mutableMapOf<String, String>()
        settingsKeys.forEach { key ->
            val parts = key.split(" ", limit = 2)
            if (parts.size == 2) {
                val namespace = parts[0]
                val setting = parts[1]
                val commandResult = AdbCommander.runShellCommand("settings get $namespace $setting")
                val value = commandResult.getOrDefault("output", "").trim()
                if (value.isNotEmpty() && value != "null") {
                    backupData[key] = value
                }
            }
        }
        return backupData
    }

    /**
     * Saves the backup data to a private file.
     */
    private fun saveBackupToFile(context: Context, profileName: String, settingsData: Map<String, String>, uninstalledPackages: List<String>): Boolean {
        if (settingsData.isEmpty() && uninstalledPackages.isEmpty()) {
            return true
        }

        val timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "optimizer_backup_${profileName.replace(" ", "_")}_${sdf.format(Date(timestamp))}.json"

        val backup = BackupData(
            timestamp = timestamp,
            profileName = profileName,
            backedUpSettings = settingsData,
            uninstalledPackages = uninstalledPackages
        )

        return try {
            val file = File(context.filesDir, fileName)
            val jsonString = json.encodeToString(backup)
            file.writeText(jsonString)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Restores the system state from a backup file.
     */
    fun restoreFromBackup(backupData: BackupData) {
        backupData.backedUpSettings.forEach { (key, value) ->
            val parts = key.split(" ", limit = 2)
            if (parts.size == 2) {
                val namespace = parts[0]
                val setting = parts[1]
                AdbCommander.runShellCommand("settings put $namespace $setting '$value'")
            }
        }

        backupData.uninstalledPackages.forEach { packageName ->
            AdbCommander.runShellCommand("pm install-existing $packageName")
        }
    }

    /**
     * Reads the backup data from a JSON file.
     */
    fun readBackupFromFile(file: File): BackupData? {
        return try {
            if (!file.exists()) return null
            val jsonString = file.readText()
            json.decodeFromString<BackupData>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
