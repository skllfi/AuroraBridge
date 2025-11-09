package com.aurorabridge.optimizer.utils

import com.aurorabridge.optimizer.adb.AdbCommander
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdbConnectionChecker(private val adbCommander: AdbCommander) {

    suspend fun checkAdbConnection(): Boolean {
        // Use a simple, non-intrusive command to check the connection.
        // "echo" is a good choice as it's harmless and available on all platforms.
        val result = withContext(Dispatchers.IO) {
            adbCommander.runAdbCommand("echo success")
        }
        return result.isSuccess && result.output.trim() == "success"
    }
}
