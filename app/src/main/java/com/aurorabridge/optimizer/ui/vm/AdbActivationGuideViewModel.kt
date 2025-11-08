package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Step(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int,
    @StringRes val buttonTextResId: Int? = null,
    val action: ((Context) -> Unit)? = null,
    val isTroubleshooting: Boolean = false
)

class AdbActivationGuideViewModel : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep = _currentStep.asStateFlow()

    private val _showSkipDialog = MutableStateFlow(false)
    val showSkipDialog = _showSkipDialog.asStateFlow()

    private val steps = listOf(
        Step(
            titleResId = R.string.adb_guide_step0_title,
            descriptionResId = R.string.adb_guide_step0_desc,
            buttonTextResId = R.string.adb_guide_open_platform_tools,
            action = { ctx -> ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com/studio/releases/platform-tools"))) }
        ),
        Step(
            titleResId = R.string.adb_guide_step1_title,
            descriptionResId = R.string.adb_guide_step1_desc,
            buttonTextResId = R.string.adb_guide_open_settings,
            action = { ctx -> ctx.startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)) }
        ),
        Step(
            titleResId = R.string.adb_guide_step2_title,
            descriptionResId = R.string.adb_guide_step2_desc
        ),
        Step(
            titleResId = R.string.adb_guide_step3_title,
            descriptionResId = R.string.adb_guide_step3_desc,
            buttonTextResId = R.string.adb_guide_open_developer_settings,
            action = { ctx -> ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)) }
        ),
        Step(
            titleResId = R.string.adb_guide_step4_title,
            descriptionResId = R.string.adb_guide_step4_desc
        ),
        Step(
            titleResId = R.string.adb_guide_step5_title,
            descriptionResId = R.string.adb_guide_step5_desc
        ),
        Step(
            titleResId = R.string.troubleshooting_title,
            descriptionResId = R.string.troubleshooting_dev_options_not_visible,
            isTroubleshooting = true
        ),
        Step(
            titleResId = R.string.troubleshooting_title,
            descriptionResId = R.string.troubleshooting_device_not_connecting,
            isTroubleshooting = true
        )
    )

    fun getSteps(): List<Step> = steps

    fun checkForDeveloperOptions(context: Context) {
        viewModelScope.launch {
            val areDeveloperOptionsEnabled = Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) == 1
            if (areDeveloperOptionsEnabled) {
                _showSkipDialog.value = true
            }
        }
    }

    fun onSkipDialogDismissed() {
        _showSkipDialog.value = false
    }

    fun onSkipToLastStep() {
        _currentStep.value = 3
        _showSkipDialog.value = false
    }

    fun onNextStep() {
        if (_currentStep.value < steps.size - 1) {
            _currentStep.value++
        }
    }

    fun onPreviousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value--
        }
    }
    
    fun onTroubleshooting() {
        _currentStep.value = 6
    }
}
