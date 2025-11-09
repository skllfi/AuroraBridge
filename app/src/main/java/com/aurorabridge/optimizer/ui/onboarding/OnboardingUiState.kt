package com.aurorabridge.optimizer.ui.onboarding

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object Checking : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Failure(val message: String) : OnboardingUiState()
}
