package com.aurorabridge.optimizer.model

import kotlinx.serialization.Serializable

@Serializable
data class OptimizationProfile(
    val name: String,
    val description: String,
    val author: String,
    val categories: List<OptimizationCategory>
)
