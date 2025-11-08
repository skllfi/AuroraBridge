package com.aurorabridge.optimizer.model

import androidx.annotation.StringRes

data class Limiter(
    @StringRes val name: Int,
    @StringRes val description: Int,
    @StringRes val solution: Int,
    val fixCommand: String? = null
)
