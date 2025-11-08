package com.aurorabridge.optimizer.analyzer

import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.Limiter

class AdbAnalyzer {

    // The report structure remains the same
    data class AnalyzerReport(
        val foundLimiters: List<Limiter>,
        val rawOutputs: Map<String, String> = emptyMap(),
        val logcatErrors: List<String> = emptyList(),
        val batteryHogs: List<Pair<String, Double>> = emptyList()
    )

    // The list of known limiters to check for
    private val knownLimiters = listOf(
        // Huawei PowerGenie
        Limiter(
            name = R.string.limiter_powergenie_name,
            description = R.string.limiter_powergenie_desc,
            solution = R.string.limiter_powergenie_solution,
            fixCommand = "pm disable-user com.huawei.powergenie",
            checkCommand = "pm list packages | grep com.huawei.powergenie"
        ),
        // MIUI PowerKeeper
        Limiter(
            name = R.string.limiter_miui_name,
            description = R.string.limiter_miui_desc,
            solution = R.string.limiter_miui_solution,
            checkCommand = "getprop ro.miui.ui.version.name"
        ),
        // MediaTek DuraSpeed
        Limiter(
            name = R.string.limiter_duraspeed_name,
            description = R.string.limiter_duraspeed_desc,
            solution = R.string.limiter_duraspeed_solution,
            checkCommand = "getprop ro.mtk_duraspeed.support"
        ),
        // Samsung Hibernation
        Limiter(
            name = R.string.limiter_hibernation_name,
            description = R.string.limiter_hibernation_desc,
            solution = R.string.limiter_hibernation_solution,
            checkCommand = "pm list packages | grep com.samsung.android.lool"
        ),
        // Aggressive Doze
        Limiter(
            name = R.string.limiter_aggressive_doze_name,
            description = R.string.limiter_aggressive_doze_desc,
            solution = R.string.limiter_aggressive_doze_solution,
            fixCommand = "dumpsys deviceidle disable light",
            checkCommand = "dumpsys deviceidle | grep mLightIdleEnabled"
        )
    )

    /**
     * Analyzes the system for known limiters using the provided command executor.
     *
     * @param commandExecutor A function that executes a shell command and returns the output.
     * @return An AnalyzerReport containing the list of found limiters.
     */
    suspend fun analyze(commandExecutor: suspend (String) -> String): AnalyzerReport {
        val foundLimiters = mutableListOf<Limiter>()

        for (limiter in knownLimiters) {
            val output = commandExecutor(limiter.checkCommand)
            if (isLimiterActive(limiter, output)) {
                foundLimiters.add(limiter)
            }
        }

        // For now, we only return the found limiters.
        // The other report fields can be populated in the future.
        return AnalyzerReport(foundLimiters = foundLimiters)
    }

    /**
     * Checks if a limiter is active based on the output of its check command.
     */
    private fun isLimiterActive(limiter: Limiter, output: String): Boolean {
        return when (limiter.name) {
            R.string.limiter_powergenie_name -> output.contains("com.huawei.powergenie")
            R.string.limiter_miui_name -> output.isNotBlank()
            R.string.limiter_duraspeed_name -> output.trim() == "1"
            R.string.limiter_hibernation_name -> output.contains("com.samsung.android.lool")
            R.string.limiter_aggressive_doze_name -> output.contains("mLightIdleEnabled=true")
            else -> false
        }
    }
}
