package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.ui.apps.BackupHistoryRepository
import com.aurorabridge.optimizer.utils.BackupAndRestoreManager
import com.aurorabridge.optimizer.utils.BackupData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class BackupInfo(
    val file: File,
    val data: BackupData
)

class BackupHistoryViewModel(private val repository: BackupHistoryRepository) : ViewModel() {

    private val _backups = MutableStateFlow<List<BackupInfo>>(emptyList())
    val backups = _backups.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun loadBackups() {
        viewModelScope.launch(Dispatchers.IO) {
            val backupFiles = repository.getBackupFiles()
            val backupInfoList = backupFiles.mapNotNull { file ->
                repository.readBackupData(file)?.let { data ->
                    BackupInfo(file, data)
                }
            }.sortedByDescending { it.data.timestamp } // Sort by most recent

            _backups.value = backupInfoList
        }
    }

    fun restoreBackup(backupInfo: BackupInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                BackupAndRestoreManager.restoreFromBackup(backupInfo.data)
                withContext(Dispatchers.Main) {
                    _snackbarMessage.emit("Backup '${backupInfo.data.profileName}' restored successfully")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _snackbarMessage.emit("Failed to restore backup: ${e.message}")
                }
            }
        }
    }

    fun deleteBackup(backupInfo: BackupInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.deleteBackupFile(backupInfo.file)) {
                withContext(Dispatchers.Main) {
                    _snackbarMessage.emit("Backup deleted successfully")
                }
                loadBackups() // Refresh the list
            } else {
                withContext(Dispatch.Main) {
                    _snackbarMessage.emit("Failed to delete backup")
                }
            }
        }
    }
}
