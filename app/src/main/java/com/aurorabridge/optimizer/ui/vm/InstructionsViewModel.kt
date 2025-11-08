package com.aurorabridge.optimizer.ui.vm

import androidx.lifecycle.ViewModel
import com.aurorabridge.optimizer.R

data class InstructionStep(
    val title: String,
    val description: String,
    val imageResId: Int
)

class InstructionsViewModel : ViewModel() {

    val instructionSteps = listOf(
        InstructionStep(
            title = "Enable Developer Options",
            description = "Go to Settings > About phone > Build number and tap it 7 times.",
            imageResId = R.drawable.step1_build_number // Placeholder
        ),
        InstructionStep(
            title = "Enable USB Debugging",
            description = "Go to Settings > System > Developer options and turn on USB debugging.",
            imageResId = R.drawable.step2_usb_debugging // Placeholder
        ),
        InstructionStep(
            title = "Enable Wireless Debugging",
            description = "(For wireless connection) Go to Settings > System > Developer options and turn on Wireless debugging.",
            imageResId = R.drawable.step3_wireless_debugging // Placeholder
        ),
        InstructionStep(
            title = "Pair with Pairing Code",
            description = "(For wireless connection) In Wireless debugging, tap 'Pair device with pairing code'. Note the code and port.",
            imageResId = R.drawable.step4_pairing_code // Placeholder
        ),
        InstructionStep(
            title = "Connect with ADB",
            description = "On your computer, run 'adb pair <IP_ADDRESS>:<PORT>' with the code from the previous step. Then run 'adb connect <IP_ADDRESS>:<PORT>'.",
            imageResId = R.drawable.step5_adb_connect // Placeholder
        )
    )
}
