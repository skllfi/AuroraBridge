package com.aurorabridge.optimizer.ui.apps

import android.content.Context
import com.aurorabridge.optimizer.utils.BackupAndRestoreManager
import com.aurorabridge.optimizer.utils.BackupData
import java.io.File

class BackupHistoryRepository(private val context: Context) {

    fun getBackupFiles(): List<File> {
        val backupDir = context.filesDir
        return backupDir.listFiles { _, name -> name.startsWith("optimizer_backup_") && name.endsWith(".json") }?.toList() ?: emptyList()
    }

    fun readBackupData(file: File): BackupData? {
        return BackupAndRestoreManager.readBackupFromFile(file)
    }

    fun deleteBackupFile(file: File): Boolean {
        return file.delete()
    }
}
