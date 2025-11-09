package com.aurorabridge.optimizer.utils

interface IAdbCommander {
    suspend fun runAdbCommandAsync(command: String, force: Boolean = false): AdbCommandResult

    suspend fun runAdbCommandsAsync(commands: List<String>, force: Boolean = false): List<AdbCommandResult>
}
