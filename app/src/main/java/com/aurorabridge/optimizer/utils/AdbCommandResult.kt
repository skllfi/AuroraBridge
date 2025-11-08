package com.aurorabridge.optimizer.utils

data class AdbCommandResult(
    val isSuccess: Boolean,
    val output: String? = null,
    val error: String? = null
)
