package com.aurorabridge.optimizer.ui.vm

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileManagementUiState {
    object Idle : ProfileManagementUiState()
    object Loading : ProfileManagementUiState()
    data class Success(val message: String) : ProfileManagementUiState()
    data class Error(val message: String) : ProfileManagementUiState()
}

@HiltViewModel
class ProfileManagementViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileManagementUiState>(ProfileManagementUiState.Idle)
    val uiState: StateFlow<ProfileManagementUiState> = _uiState

    fun importProfile(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ProfileManagementUiState.Loading
            try {
                application.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val content = inputStream.bufferedReader().use { it.readText() }
                    // TODO: Process the imported content (e.g., parse JSON and apply settings)
                    _uiState.value = ProfileManagementUiState.Success("Profile imported successfully.")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileManagementUiState.Error("Failed to import profile: ${e.message}")
            }
        }
    }

    fun exportProfile(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ProfileManagementUiState.Loading
            try {
                application.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    // TODO: Generate the actual profile content to be exported
                    val content = "{\"profile_name\": \"My Optimization Profile\"}"
                    outputStream.write(content.toByteArray())
                    _uiState.value = ProfileManagementUiState.Success("Profile exported successfully.")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileManagementUiState.Error("Failed to export profile: ${e.message}")
            }
        }
    }
}
