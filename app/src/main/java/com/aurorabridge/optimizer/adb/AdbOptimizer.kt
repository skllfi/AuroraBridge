package com.aurorabridge.optimizer.adb

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object AdbOptimizer {
    private const val TAG = "AdbOptimizer"

    fun applyProfile(profileName: String): Map<String, String> {
        val commands = AdbProfiles.getCommandsForProfile(profileName)
        if (commands.isEmpty()) {
            Log.w(TAG, "No commands found for profile: $profileName")
            return mapOf("error" to "Profile not found or is empty.")
        }
        return runCommands(commands)
    }

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
