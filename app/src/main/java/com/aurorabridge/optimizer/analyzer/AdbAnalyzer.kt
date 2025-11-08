package com.aurorabridge.optimizer.analyzer

import android.util.Log
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.Limiter

object AdbAnalyzer {
    private const val TAG = "AdbAnalyzer"

    data class AnalyzerReport(
        val foundLimiters: List<Limiter>,
        val rawOutputs: Map<String, String>,
        val logcatErrors: List<String>,
        val batteryHogs: List<Pair<String, Double>>
    )

    private val knownLimiters = listOf(
        Limiter(
            name = R.string.limiter_powergenie_name,
            description = R.string.limiter_powergenie_desc,
            solution = R.string.limiter_powergenie_solution,
            fixCommand = "pm disable-user com.huawei.powergenie"
        ),
        Limiter(
            name = R.string.limiter_miui_name,
            description = R.string.limiter_miui_desc,
            solution = R.string.limiter_miui_solution
        ),
        Limiter(
            name = R.string.limiter_duraspeed_name,
            description = R.string.limiter_duraspeed_desc,
            solution = R.string.limiter_duraspeed_solution
        ),
        Limiter(
            name = R.string.limiter_hibernation_name,
            description = R.string.limiter_hibernation_desc,
            solution = R.string.limiter_hibernation_solution
        ),
        Limiter(
            name = R.string.limiter_aggressive_doze_name,
            description = R.string.limiter_aggressive_doze_desc,
            solution = R.string.limiter_aggressive_doze_solution,
            fixCommand = "dumpsys deviceidle disable light"
        )
    )

    fun runFullAnalysis(): AnalyzerReport {
        val cmds = mapOf(
            "pm_list" to arrayOf("sh", "-c", "pm list packages"),
            "dumpsys_deviceidle" to arrayOf("sh", "-c", "dumpsys deviceidle"),
            "dumpsys_battery" to arrayOf("sh", "-c", "dumpsys battery"),
            "powergenie_check" to arrayOf("sh", "-c", "pm list packages | grep powergenie || true"),
            "miui_check" to arrayOf("sh", "-c", "getprop ro.miui.ui.version.name || true"),
            "duraspeed_check" to arrayOf("sh", "-c", "getprop ro.mtk_duraspeed.support || true"),
            "hibernation_check" to arrayOf("sh", "-c", "pm list packages | grep hibernation || true"),
            "doze_check" to arrayOf("sh", "-c", "dumpsys deviceidle | grep mLightIdleEnabled || true")
        )
        val outputs = mutableMapOf<String, String>()
        val found = mutableListOf<Limiter>()

        for ((k, cmd) in cmds) {
            try {
                val p = Runtime.getRuntime().exec(cmd)
                val reader = p.inputStream.bufferedReader()
                val out = reader.readText()
                outputs[k] = out

                when (k) {
                    "powergenie_check" -> if (out.contains("powergenie", true)) knownLimiters.find { it.name == R.string.limiter_powergenie_name }?.let { found.add(it) }
                    "miui_check" -> if (out.isNotBlank()) knownLimiters.find { it.name == R.string.limiter_miui_name }?.let { found.add(it) }
                    "duraspeed_check" -> if (out.trim() == "1") knownLimiters.find { it.name == R.string.limiter_duraspeed_name }?.let { found.add(it) }
                    "hibernation_check" -> if (out.contains("hibernation", true)) knownLimiters.find { it.name == R.string.limiter_hibernation_name }?.let { found.add(it) }
                    "doze_check" -> if (out.contains("mLightIdleEnabled=true")) knownLimiters.find { it.name == R.string.limiter_aggressive_doze_name }?.let { found.add(it) }
                }
            } catch (e: Exception) {
                Log.w(TAG, "cmd failed: $k -> $e")
                outputs[k] = "error: $e"
            }
        }

        val logcatErrors = analyzeLogcat()
        val batteryHogs = parseBatteryStats(outputs["dumpsys_battery"] ?: "")

        return AnalyzerReport(found, outputs, logcatErrors, batteryHogs)
    }

    private fun analyzeLogcat(): List<String> {
        val errors = mutableListOf<String>()
        try {
            val p = Runtime.getRuntime().exec(arrayOf("sh", "-c", "logcat -d -v time | grep -E 'ANR in|Broadcast timeout'"))
            val reader = p.inputStream.bufferedReader()
            errors.addAll(reader.readLines())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze logcat", e)
        }
        return errors
    }

    private fun parseBatteryStats(batteryStatsOutput: String): List<Pair<String, Double>> {
        val batteryHogs = mutableListOf<Pair<String, Double>>()
        val regex = Regex("Uid u0a\\d+: ([\\d\\.]+) \\(\\d+\\) (.*)")
        batteryStatsOutput.lines().forEach { line ->
            val match = regex.find(line)
            if (match != null) {
                val (usage, _, name) = match.destructured
                batteryHogs.add(Pair(name.trim(), usage.toDouble()))
            }
        }
        return batteryHogs.sortedByDescending { it.second }.take(5)
    }
    fun applyFix(command: String) {
        try {
            Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to apply fix: $command", e)
        }
    }
}
