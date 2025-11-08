package com.aurorabridge.optimizer.core.adb

/**
 * AdbAnalyzer is responsible for scanning the device to detect active
 * system limiters and bloatware that can interfere with background processes.
 *
 * It executes a series of ADB commands to probe for known packages and services
 * from different manufacturers (e.g., Huawei, Xiaomi, Samsung).
 *
 * The results can be used to recommend specific optimization profiles to the user.
 */
class AdbAnalyzer {

    // A map of limiter names to the shell command used to detect them.
    private val limitersToProbe = mapOf(
        "PowerGenie (Huawei)" to "pm list packages com.huawei.powergenie",
        "MIUI PowerKeeper (Xiaomi)" to "pm list packages com.miui.powerkeeper",
        "Smart Manager (Samsung)" to "pm list packages com.samsung.android.sm",
        "OnePlus Background Process" to "pm list packages com.oneplus.backgroundp",
        "ColorOS GuardElf (Oppo/Realme)" to "pm list packages com.coloros.oppoguardelf"
    )

    /**
     * Runs a system analysis to find active limiters.
     *
     * @param commandExecutor An executor capable of running shell commands.
     * @return A list of names of the limiters that were found.
     */
    suspend fun analyze(commandExecutor: suspend (String) -> String): List<String> {
        val foundLimiters = mutableListOf<String>()

        for ((name, command) in limitersToProbe) {
            val output = commandExecutor(command).trim()
            // The `pm list packages` command returns the package name if found.
            if (output.isNotEmpty()) {
                foundLimiters.add(name)
            }
        }
        return foundLimiters
    }
}
