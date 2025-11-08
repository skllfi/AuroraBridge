package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.ui.apps.BackupHistoryRepository
import com.aurorabridge.optimizer.utils.BackupAndRestoreManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackupHistoryViewModel(private val repository: BackupHistoryRepository) : ViewModel() {

    private val _backups = MutableStateFlow<List<String>>(emptyList())
    val backups = _backups.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun loadBackups() {
        viewModelScope.launch {
            _backups.value = repository.getBackupList()
        }
    }

    fun restoreBackup(context: Context, backupName: String) {
        viewModelScope.launch {
            val backupData = repository.readBackupFromFile(backupName)
            if (backupData != null) {
                BackupAndRestoreManager.applySettingsFromBackup(backupData)
                _snackbarMessage.emit("Backup restored successfully")
            } else {
                _snackbarMessage.emit("Failed to restore backup")
            }
        }
    }

    fun deleteBackup(backupName: String) {
        viewModelScope.launch {
            if (repository.deleteBackup(backupName)) {
                _snackbarMessage.emit("Backup deleted successfully")
                loadBackups()
            } else {
                _snackbarMessage.emit("Failed to delete backup")
            }
        }
    }
}