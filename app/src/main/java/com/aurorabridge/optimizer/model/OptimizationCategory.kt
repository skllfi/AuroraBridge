package com.aurorabridge.optimizer.model

import kotlinx.serialization.Serializable

@Serializable
data class OptimizationCategory(
    val name: String,
    val description: String,
    val commands: List<OptimizationCommand>
)
