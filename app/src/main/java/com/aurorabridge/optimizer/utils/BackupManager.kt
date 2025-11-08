
package com.aurorabridge.optimizer.utils

import android.content.Context
import com.aurorabridge.optimizer.model.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class BackupManager(private val context: Context) {

    private val json = Json { prettyPrint = true }

    fun exportSettings(settings: Settings): File? {
        return try {
            val jsonString = json.encodeToString(settings)
            val file = File(context.cacheDir, "settings_backup.json")
            file.writeText(jsonString)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun importSettings(file: File): Settings? {
        return try {
            val jsonString = file.readText()
            json.decodeFromString<Settings>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
