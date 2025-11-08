package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aurorabridge.optimizer.ui.apps.BackupHistoryRepository

class BackupHistoryViewModelFactory(private val repository: BackupHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BackupHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BackupHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}