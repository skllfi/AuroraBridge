package com.aurorabridge.optimizer.ui.apps

import android.content.Context
import com.aurorabridge.optimizer.utils.BackupAndRestoreManager
import java.io.File

class BackupHistoryRepository(private val context: Context) {

    fun getBackupList(): List<String> {
        val files = context.filesDir.listFiles { _, name -> name.startsWith("optimizer_backup_") && name.endsWith(".json") }
        return files?.map { it.name }?.sortedDescending() ?: emptyList()
    }

    fun readBackupFromFile(fileName: String): Map<String, String>? {
        val file = File(context.filesDir, fileName)
        return BackupAndRestoreManager.readBackupFromFile(file)
    }

    fun deleteBackup(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return file.delete()
    }
}