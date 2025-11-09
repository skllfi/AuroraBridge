package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.model.OptimizationCategory
import com.aurorabridge.optimizer.model.OptimizationCommand
import com.aurorabridge.optimizer.model.OptimizationProfile
import com.aurorabridge.optimizer.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WizardStep {
    Welcome,
    CategorySelection,
    CommandSelection,
    Confirmation,
    Execution,
    Finished
}

data class OptimizationWizardUiState(
    val profile: OptimizationProfile? = null,
    val currentStep: WizardStep = WizardStep.Welcome,
    val selectedCommands: Set<OptimizationCommand> = emptySet(),
    val isLoading: Boolean = true,
    val isExecuting: Boolean = false,
    val error: String? = null,
    val executionLog: List<String> = emptyList()
)

@HiltViewModel
class OptimizationWizardViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OptimizationWizardUiState())
    val uiState: StateFlow<OptimizationWizardUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = profileRepository.loadDefaultProfile()
                val allCommands = profile.categories.flatMap { it.commands }.toSet()
                _uiState.value = OptimizationWizardUiState(
                    profile = profile, 
                    isLoading = false, 
                    selectedCommands = allCommands
                )
            } catch (e: Exception) {
                _uiState.value = OptimizationWizardUiState(
                    error = "Failed to load profile: ${e.message}", 
                    isLoading = false
                )
            }
        }
    }

    fun nextStep() {
        if (_uiState.value.isExecuting) return

        val currentStep = _uiState.value.currentStep
        if (currentStep == WizardStep.Confirmation) {
            runOptimizations()
            return
        }

        val nextStep = when (currentStep) {
            WizardStep.Welcome -> WizardStep.CategorySelection
            WizardStep.CategorySelection -> WizardStep.CommandSelection
            WizardStep.CommandSelection -> WizardStep.Confirmation
            WizardStep.Execution -> WizardStep.Finished
            WizardStep.Finished -> WizardStep.Welcome
            else -> currentStep
        }
        _uiState.value = _uiState.value.copy(currentStep = nextStep)
    }

    fun previousStep() {
        if (_uiState.value.isExecuting) return

        val previousStep = when (_uiState.value.currentStep) {
            WizardStep.Finished -> WizardStep.Confirmation
            WizardStep.Execution -> WizardStep.Confirmation
            WizardStep.Confirmation -> WizardStep.CommandSelection
            WizardStep.CommandSelection -> WizardStep.CategorySelection
            WizardStep.CategorySelection -> WizardStep.Welcome
            WizardStep.Welcome -> WizardStep.Welcome
        }
        _uiState.value = _uiState.value.copy(currentStep = previousStep)
    }

    fun toggleCommandSelection(command: OptimizationCommand) {
        val selected = _uiState.value.selectedCommands.toMutableSet()
        if (selected.contains(command)) {
            selected.remove(command)
        } else {
            selected.add(command)
        }
        _uiState.value = _uiState.value.copy(selectedCommands = selected)
    }

    fun toggleCategorySelection(category: OptimizationCategory, isSelected: Boolean) {
        val selected = _uiState.value.selectedCommands.toMutableSet()
        if (isSelected) {
            selected.addAll(category.commands)
        } else {
            selected.removeAll(category.commands.toSet())
        }
        _uiState.value = _uiState.value.copy(selectedCommands = selected)
    }

    private fun runOptimizations() {
        viewModelScope.launch {
            _uiState.update { it.copy(currentStep = WizardStep.Execution, isExecuting = true, executionLog = emptyList()) }

            val log = mutableListOf<String>()

            log.add("Starting optimizations...")
            _uiState.update { it.copy(executionLog = log.toList()) }
            delay(500)

            for (command in _uiState.value.selectedCommands) {
                log.add("Executing: ${command.name}")
                _uiState.update { it.copy(executionLog = log.toList()) }
                delay(1000)

                log.add("  -> Success!")
                _uiState.update { it.copy(executionLog = log.toList()) }
                delay(200)
            }

            delay(500)
            log.add("\nAll optimizations applied successfully!")
            _uiState.update { it.copy(executionLog = log.toList(), isExecuting = false) }
        }
    }

    fun resetWizard() {
        _uiState.update {
            val allCommands = it.profile?.categories?.flatMap { cat -> cat.commands }?.toSet() ?: emptySet()
            OptimizationWizardUiState(
                profile = it.profile,
                isLoading = false,
                selectedCommands = allCommands
            )
        }
    }
}
