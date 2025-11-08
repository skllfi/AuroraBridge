package com.aurorabridge.optimizer.utils

import android.content.Context
import com.aurorabridge.optimizer.adb.AdbCommander
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupAndRestoreManager {

    private const val BACKUP_FILE_NAME = "optimizer_backup.properties"
    private const val TIMESTAMP_KEY = "backup_timestamp"

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
     * Saves the backup data to a private file, including a timestamp.
     */
    fun saveBackupToFile(context: Context, backupData: Map<String, String>): Boolean {
        return try {
            val file = File(context.filesDir, BACKUP_FILE_NAME)
            file.bufferedWriter().use { out ->
                // Add timestamp to the backup
                out.write("$TIMESTAMP_KEY=${System.currentTimeMillis()}\n")
                backupData.forEach { (key, value) ->
                    val sanitizedValue = value.replace("\n", "\\n")
                    out.write("$key=$sanitizedValue\n")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Reads the backup data from the file, excluding the timestamp.
     */
    fun readBackupFromFile(context: Context): Map<String, String>? {
        return try {
            val file = File(context.filesDir, BACKUP_FILE_NAME)
            if (!file.exists()) return null

            val backupData = mutableMapOf<String, String>()
            file.bufferedReader().forEachLine { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2 && parts[0] != TIMESTAMP_KEY) {
                    backupData[parts[0]] = parts[1].replace("\\n", "\n")
                }
            }
            backupData
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Retrieves the timestamp of the last backup.
     */
    fun getBackupTimestamp(context: Context): String {
        return try {
            val file = File(context.filesDir, BACKUP_FILE_NAME)
            if (!file.exists()) return "No backup found"

            file.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split("=", limit = 2)
                    if (parts.size == 2 && parts[0] == TIMESTAMP_KEY) {
                        val timestamp = parts[1].toLongOrNull()
                        if (timestamp != null) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            return@getBackupTimestamp "Last backup: ${sdf.format(Date(timestamp))}"
                        }
                    }
                }
            }
            "No backup found"
        } catch (e: IOException) {
            e.printStackTrace()
            "Error reading backup"
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
