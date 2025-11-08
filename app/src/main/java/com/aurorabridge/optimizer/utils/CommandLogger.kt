package com.aurorabridge.optimizer.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A singleton object for logging all ADB commands executed by the application.
 *
 * This helps in debugging and provides a history of operations for the user.
 */
object CommandLogger {

    /**
     * Represents a single command log entry.
     */
    data class LogEntry(
        val command: String,
        val success: Boolean,
        val output: String,
        val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    )

    private val logHistory = mutableListOf<LogEntry>()

    /**
     * Adds a new entry to the log.
     *
     * @param command The executed ADB command.
     * @param success Whether the command was successful.
     * @param output The output of the command (stdout or stderr).
     */
    fun log(command: String, success: Boolean, output: String) {
        logHistory.add(LogEntry(command, success, output))
    }

    /**
     * Returns the complete log history.
     */
    fun getHistory(): List<LogEntry> {
        return logHistory.toList()
    }

    /**
     * Clears the entire log history.
     */
    fun clearHistory() {
        logHistory.clear()
    }
}
