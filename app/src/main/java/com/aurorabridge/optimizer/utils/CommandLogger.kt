package com.aurorabridge.optimizer.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommandLogger {

    data class LogEntry(
        val command: String,
        val success: Boolean,
        val details: String,
        val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
        val isSafeMode: Boolean = false
    )

    private val logHistory = mutableListOf<LogEntry>()

    fun log(command: String, isSuccess: Boolean, details: String, isSafeMode: Boolean = false) {
        logHistory.add(
            LogEntry(
                command = command,
                success = isSuccess,
                details = details,
                isSafeMode = isSafeMode
            )
        )
    }

    fun getHistory(): List<LogEntry> {
        return logHistory.toList()
    }

    fun clearHistory() {
        logHistory.clear()
    }
}
