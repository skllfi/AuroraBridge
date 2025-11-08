package com.aurorabridge.optimizer.utils

import android.content.Context
import com.aurorabridge.optimizer.optimizer.OptimizationProfile
import java.io.File
import java.io.IOException

object BackupManager {

    private const val BACKUP_FILE_NAME = "optimizer_backup.txt"

    fun createBackup(context: Context, profile: OptimizationProfile): Boolean {
        try {
            val backupFile = File(context.filesDir, BACKUP_FILE_NAME)
            val writer = backupFile.bufferedWriter()

            profile.commands.forEach { command ->
                val parts = command.split(" ")
                if (parts.size >= 4 && parts[0] == "settings") {
                    val namespace = parts[2]
                    val key = parts[3]

                    val getCurrentSettingCmd = "settings get $namespace $key"
                    val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", getCurrentSettingCmd))
                    val originalValue = p.inputStream.bufferedReader().readText().trim()

                    if (originalValue.isNotEmpty() && originalValue != "null") {
                        val restoreCommand = "settings put $namespace $key $originalValue\n"
                        writer.write(restoreCommand)
                    }
                }
            }
            writer.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun restoreBackup(context: Context): Boolean {
        val backupFile = File(context.filesDir, BACKUP_FILE_NAME)
        if (!backupFile.exists()) return false

        return try {
            backupFile.readLines().forEach { command ->
                Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun hasBackup(context: Context): Boolean {
        return File(context.filesDir, BACKUP_FILE_NAME).exists()
    }

}
