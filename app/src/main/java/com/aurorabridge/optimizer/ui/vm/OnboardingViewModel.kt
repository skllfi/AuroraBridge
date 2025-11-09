package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.repository.SettingsRepository
import com.aurorabridge.optimizer.ui.onboarding.OnboardingUiState
import com.aurorabridge.optimizer.utils.AdbConnectionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val adbConnectionChecker: AdbConnectionChecker,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun checkAdbConnection() {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Checking
            try {
                val isSuccess = adbConnectionChecker.checkAdbConnection()
                if (isSuccess) {
                    _uiState.value = OnboardingUiState.Success
                } else {
                    _uiState.value = OnboardingUiState.Failure("Connection failed. Ensure wireless debugging is enabled and you are on the same Wi-Fi network.")
                }
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Failure("An error occurred: ${e.message}")
            }
        }
    }

    fun setOnboardingComplete() {
        settingsRepository.setOnboardingComplete(true)
    }

    fun resetState() {
        _uiState.value = OnboardingUiState.Idle
    }
}
