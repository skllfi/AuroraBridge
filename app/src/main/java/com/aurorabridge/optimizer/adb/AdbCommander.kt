
package com.aurorabridge.optimizer.adb

import java.io.BufferedReader
import java.io.InputStreamReader

data class CommandResult(val isSuccess: Boolean, val output: String)

object AdbCommander {

    fun runCommand(command: String): CommandResult {
        return try {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readText()
            val exitCode = process.waitFor()
            CommandResult(exitCode == 0, output)
        } catch (e: Exception) {
            CommandResult(false, e.message ?: "Unknown error")
        }
    }

    fun enableAdbWifi(): CommandResult {
        val command = "setprop service.adb.tcp.port 5555"
        return runCommand(command)
    }

    fun disableAdbWifi(): CommandResult {
        val command = "setprop service.adb.tcp.port -1"
        return runCommand(command)
    }
}
