package com.aurorabridge.optimizer.adb

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object AdbOptimizer {
    private const val TAG = "AdbOptimizer"

    fun runCommands(cmds: List<String>): Map<String,String> {
        val results = mutableMapOf<String,String>()
        for (cmd in cmds) {
            try {
                val p = Runtime.getRuntime().exec(arrayOf("sh","-c",cmd))
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                val out = reader.readText()
                results[cmd] = out
            } catch (e: Exception) {
                Log.e(TAG, "cmd failed ${"$"}cmd", e)
                results[cmd] = "error: ${"$"}e"
            }
        }
        return results
    }
}
