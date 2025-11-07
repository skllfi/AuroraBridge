package com.aurorabridge.optimizer.adb

import android.util.Log

object AdbAnalyzer {
    private const val TAG = "AdbAnalyzer"

    data class AnalyzerReport(val foundLimiters: List<String>, val rawOutputs: Map<String,String>)

    // Run a set of diagnostic commands and collect outputs
    fun runFullAnalysis(): AnalyzerReport {
        val cmds = mapOf(
            "pm_list" to arrayOf("sh","-c","pm list packages"),
            "dumpsys_deviceidle" to arrayOf("sh","-c","dumpsys deviceidle"),
            "dumpsys_battery" to arrayOf("sh","-c","dumpsys battery"),
            "powergenie_check" to arrayOf("sh","-c","pm list packages | grep powergenie || true"),
            "miui_check" to arrayOf("sh","-c","getprop ro.miui.ui.version.name || true")
        )
        val outputs = mutableMapOf<String,String>()
        val found = mutableListOf<String>()
        for ((k, cmd) in cmds) {
            try {
                val p = Runtime.getRuntime().exec(cmd)
                val reader = p.inputStream.bufferedReader()
                val out = reader.readText()
                outputs[k] = out
                if (out.contains("powergenie", true)) found.add("PowerGenie (Huawei/Honor)")
                if (out.contains("miui", true)) found.add("MIUI (Xiaomi)")
            } catch (e: Exception) {
                Log.w(TAG, "cmd failed: ${"$"}k -> ${"$"}e")
                outputs[k] = "error: ${"$"}e"
            }
        }
        return AnalyzerReport(found, outputs)
    }
}
