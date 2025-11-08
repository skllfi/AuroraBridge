package com.aurorabridge.optimizer.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class AdbCommander : IAdbCommander {

    override suspend fun runAdbCommandAsync(command: String): AdbCommandResult = withContext(Dispatchers.IO) {
        try {
            // Connect to the ADB server
            val socket = Socket("localhost", 5037)
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            // 1. Select the active device
            val selectDeviceCommand = "host:transport-any"
            val formattedSelectCommand = String.format("%04x%s", selectDeviceCommand.length, selectDeviceCommand)
            writer.print(formattedSelectCommand)
            writer.flush()

            val selectResponse = reader.readLine()
            if (selectResponse == null || !selectResponse.startsWith("OKAY")) {
                return@withContext AdbCommandResult(isSuccess = false, error = "Failed to select device: ${selectResponse ?: "No response"}")
            }

            // 2. Execute the shell command
            val shellCommand = "shell:$command"
            val formattedShellCommand = String.format("%04x%s", shellCommand.length, shellCommand)
            writer.print(formattedShellCommand)
            writer.flush()

            val shellResponse = reader.readLine()
            if (shellResponse == null || !shellResponse.startsWith("OKAY")) {
                val errorOutput = reader.readText() // Read the rest of the error message
                return@withContext AdbCommandResult(isSuccess = false, error = "Command failed: ${shellResponse ?: ""} - $errorOutput")
            }

            // 3. Read the full output of the command
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            AdbCommandResult(isSuccess = true, output = output.toString())
        } catch (e: Exception) {
            AdbCommandResult(isSuccess = false, error = e.message ?: "Exception occurred")
        }
    }
}
