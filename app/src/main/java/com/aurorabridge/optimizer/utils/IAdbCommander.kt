package com.aurorabridge.optimizer.utils

interface IAdbCommander {
    suspend fun runAdbCommandAsync(command: String): AdbCommandResult
}
