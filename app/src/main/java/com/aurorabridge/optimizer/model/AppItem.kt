package com.aurorabridge.optimizer.model

import android.graphics.drawable.Drawable

enum class AppCategory {
    USER,
    SYSTEM,
    BLOATWARE
}

data class AppItem(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val category: AppCategory,
    val permissions: List<String> = emptyList()
)
