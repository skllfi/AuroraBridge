package com.aurorabridge.optimizer.utils

import com.aurorabridge.optimizer.R

sealed class ParsedCommand {
    data class UninstallPackage(val packageName: String) : ParsedCommand()
    data class PutSetting(val namespace: String, val key: String, val value: String) : ParsedCommand()
    data class Unknown(val command: String) : ParsedCommand()
}

object CommandParser {

    private val uninstallRegex = """pm\s+uninstall\s+(-k\s+)?--user\s+\d+\s+([\w\.]+)""".toRegex()
    private val settingsPutRegex = """settings\s+put\s+(\w+)\s+([\w\.\/]+)\s+([\w\.\/]+)""".toRegex()

    fun parse(commands: List<String>): List<ParsedCommand> {
        return commands.map { command ->
            val uninstallMatch = uninstallRegex.find(command)
            val settingsMatch = settingsPutRegex.find(command)

            when {
                uninstallMatch != null -> {
                    val packageName = uninstallMatch.groupValues[2]
                    ParsedCommand.UninstallPackage(packageName)
                }
                settingsMatch != null -> {
                    val namespace = settingsMatch.groupValues[1]
                    val key = settingsMatch.groupValues[2]
                    val value = settingsMatch.groupValues[3]
                    ParsedCommand.PutSetting(namespace, key, value)
                }
                else -> {
                    ParsedCommand.Unknown(command)
                }
            }
        }
    }\n}
