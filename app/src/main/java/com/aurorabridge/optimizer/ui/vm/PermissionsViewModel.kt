package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.model.AppItem
import com.aurorabridge.optimizer.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PermissionsUiState {
    object Loading : PermissionsUiState()
    data class Success(val app: AppItem) : PermissionsUiState()
    data class Error(val message: String) : PermissionsUiState()
}

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val appRepository: AppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<PermissionsUiState>(PermissionsUiState.Loading)
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    init {
        val packageName = savedStateHandle.get<String>("packageName")
        if (packageName != null) {
            loadAppPermissions(packageName)
        } else {
            _uiState.value = PermissionsUiState.Error("App package name not provided.")
        }
    }

    private fun loadAppPermissions(packageName: String) {
        viewModelScope.launch {
            _uiState.value = PermissionsUiState.Loading
            val app = appRepository.getApp(packageName)
            if (app != null) {
                _uiState.value = PermissionsUiState.Success(app)
            } else {
                _uiState.value = PermissionsUiState.Error("App not found.")
            }
        }
    }
}
