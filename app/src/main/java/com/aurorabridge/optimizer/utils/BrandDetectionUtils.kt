package com.aurorabridge.optimizer.utils

import android.os.Build
import java.util.Locale

object BrandDetectionUtils {
    fun getManufacturer(): String {
        return Build.MANUFACTURER.lowercase(Locale.getDefault())
    }
}
