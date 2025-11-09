package com.aurorabridge.optimizer.model

import kotlinx.serialization.Serializable

@Serializable
data class OptimizationCommand(
    val name: String,
    val description: String,
    val command: String
)
