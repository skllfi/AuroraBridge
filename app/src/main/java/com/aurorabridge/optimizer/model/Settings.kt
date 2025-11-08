
package com.aurorabridge.optimizer.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val version: Int = 1,
    val experimentalFeaturesEnabled: Boolean
)
