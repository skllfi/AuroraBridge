package com.aurorabridge.optimizer.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

object AdbCommander : IAdbCommander {

    private lateinit var settingsManager: SettingsManager

    fun initialize(context: Context) {
        settingsManager = SettingsManager(context)
    }

    override suspend fun runAdbCommandAsync(command: String, force: Boolean): AdbCommandResult = withContext(Dispatchers.IO) {
        if (settingsManager.isSafeModeEnabled() && !force) {
            CommandLogger.log(command, isSuccess = true, details = "[SAFE MODE] Command not executed.", isSafeMode = true)
            return@withContext AdbCommandResult(isSuccess = true, output = "Safe Mode: Command logged but not executed.")
        }

        val result = try {
            val socket = Socket("localhost", 5037)
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            val selectDeviceCommand = "host:transport-any"
            val formattedSelectCommand = String.format("%04x%s", selectDeviceCommand.length, selectDeviceCommand)
            writer.print(formattedSelectCommand)
            writer.flush()

            val selectResponse = reader.readLine()
            if (selectResponse == null || !selectResponse.startsWith("OKAY")) {
                AdbCommandResult(isSuccess = false, error = "Failed to select device: ${selectResponse ?: "No response"}")
            } else {
                val shellCommand = "shell:$command"
                val formattedShellCommand = String.format("%04x%s", shellCommand.length, shellCommand)
                writer.print(formattedShellCommand)
                writer.flush()

                val shellResponse = reader.readLine()
                if (shellResponse == null || !shellResponse.startsWith("OKAY")) {
                    val errorOutput = reader.readText()
                    AdbCommandResult(isSuccess = false, error = "Command failed: ${shellResponse ?: ""} - $errorOutput")
                } else {
                    val output = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        output.append(line).append("
")
                    }
                    AdbCommandResult(isSuccess = true, output = output.toString())
                }
            }
        } catch (e: Exception) {
            AdbCommandResult(isSuccess = false, error = e.message ?: "Exception occurred")
        }

        CommandLogger.log(command, result.isSuccess, result.output ?: result.error ?: "No output")

        return@withContext result
    }

    override suspend fun runAdbCommandsAsync(commands: List<String>, force: Boolean): List<AdbCommandResult> {
        val results = mutableListOf<AdbCommandResult>()
        for (command in commands) {
            results.add(runAdbCommandAsync(command, force))
        }
        return results
    }

    suspend fun uninstallApp(packageName: String, force: Boolean = false): AdbCommandResult {
        val command = "pm uninstall -k --user 0 $packageName"
        return runAdbCommandAsync(command, force)
    }
}
