package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.adb.AdbCommander
import com.aurorabridge.optimizer.adb.AdbOptimizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdbCompanionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AdbCompanionUiState>(AdbCompanionUiState.RequiresConfirmation)
    val uiState: StateFlow<AdbCompanionUiState> = _uiState.asStateFlow()

    private val _showConfirmDialog = MutableStateFlow<String?>(null)
    val showConfirmDialog: StateFlow<String?> = _showConfirmDialog.asStateFlow()

    fun onWarningConfirmed() {
        _uiState.value = AdbCompanionUiState.Idle
    }

    fun applyOptimizationProfile(profileName: String) {
        _showConfirmDialog.value = profileName
    }

    fun confirmApplyOptimizationProfile(profileName: String) {
        _showConfirmDialog.value = null
        viewModelScope.launch {
            _uiState.value = AdbCompanionUiState.Loading
            val result = AdbOptimizer.applyProfile(profileName)
            _uiState.value = AdbCompanionUiState.Success("Applied profile '$profileName' with result: $result")
        }
    }

    fun dismissDialog() {
        _showConfirmDialog.value = null
    }

    fun enableAdbWifi() {
        viewModelScope.launch {
            _uiState.value = AdbCompanionUiState.Loading
            val result = AdbCommander.enableAdbWifi()
            _uiState.value = AdbCompanionUiState.Success("ADB Wi-Fi enabled with result: $result")
        }
    }

    fun disableAdbWifi() {
        viewModelScope.launch {
            _uiState.value = AdbCompanionUiState.Loading
            val result = AdbCommander.disableAdbWifi()
            _uiState.value = AdbCompanionUiState.Success("ADB Wi-Fi disabled with result: $result")
        }
    }
}

sealed interface AdbCompanionUiState {
    object RequiresConfirmation : AdbCompanionUiState
    object Idle : AdbCompanionUiState
    object Loading : AdbCompanionUiState
    data class Success(val message: String) : AdbCompanionUiState
    data class Error(val message: String) : AdbCompanionUiState
}
