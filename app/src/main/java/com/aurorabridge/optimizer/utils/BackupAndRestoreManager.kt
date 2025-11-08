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
    val settings: Map<String, String>
)

object BackupAndRestoreManager {

    /**
     * Creates a snapshot of current settings based on a list of settings keys.
     */
    fun createSettingsSnapshot(settingsKeys: List<String>): Map<String, String> {
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
     * Saves the backup data to a private file in JSON format with a timestamp in the filename.
     */
    fun saveBackupToFile(context: Context, settingsData: Map<String, String>): Boolean {
        val timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "optimizer_backup_${sdf.format(Date(timestamp))}.json"
        val backup = BackupData(timestamp, settingsData)
        return try {
            val file = File(context.filesDir, fileName)
            val jsonString = Json.encodeToString(backup)
            file.writeText(jsonString)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Reads the backup data from the JSON file.
     */
    fun readBackupFromFile(file: File): Map<String, String>? {
        return try {
            if (!file.exists()) return null

            val jsonString = file.readText()
            val backup = Json.decodeFromString<BackupData>(jsonString)
            backup.settings
        } catch (e: Exception) { // Catching a broader exception for JSON parsing issues
            e.printStackTrace()
            null
        }
    }

    /**
     * Restores settings from a given backup data map.
     */
    fun applySettingsFromBackup(backupData: Map<String, String>) {
        backupData.forEach { (key, value) ->
            val parts = key.split(" ", limit = 2)
            if (parts.size == 2) {
                val namespace = parts[0]
                val setting = parts[1]
                AdbCommander.runShellCommand("settings put $namespace $setting $value")
            }
        }
    }
}
