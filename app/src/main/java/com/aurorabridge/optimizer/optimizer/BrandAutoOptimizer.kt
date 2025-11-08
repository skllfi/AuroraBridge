package com.aurorabridge.optimizer.optimizer

import android.content.Context
import android.os.Build
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.DeviceBrand
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class BrandAutoOptimizer {

    fun getDeviceBrand(): DeviceBrand {
        return when (Build.MANUFACTURER.toLowerCase()) {
            "xiaomi" -> DeviceBrand.XIAOMI
            "huawei" -> DeviceBrand.HUAWEI
            "google" -> DeviceBrand.PIXEL
            "samsung" -> DeviceBrand.SAMSUNG
            "oneplus" -> DeviceBrand.ONEPLUS
            "oppo" -> DeviceBrand.OPPO
            "vivo" -> DeviceBrand.VIVO
            "realme" -> DeviceBrand.REALME
            else -> DeviceBrand.UNKNOWN
        }
    }

    fun getOptimizationCommands(brand: DeviceBrand, packageName: String, context: Context): List<String> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.brand_optimizations)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            val jsonObject = JSONObject(jsonString)

            val brandKey = brand.name
            if (jsonObject.has(brandKey)) {
                val commandsArray = jsonObject.getJSONArray(brandKey)
                (0 until commandsArray.length())
                    .map { commandsArray.getString(it) }
                    .map { it.replace("$packageName", packageName) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getOptimizationDescription(brand: DeviceBrand, context: Context): String {
        val resourceId = when (brand) {
            DeviceBrand.XIAOMI -> R.string.optimization_desc_xiaomi
            DeviceBrand.HUAWEI -> R.string.optimization_desc_huawei
            DeviceBrand.PIXEL -> R.string.optimization_desc_pixel
            DeviceBrand.SAMSUNG -> R.string.optimization_desc_samsung
            DeviceBrand.ONEPLUS -> R.string.optimization_desc_oneplus
            DeviceBrand.OPPO -> R.string.optimization_desc_oppo
            DeviceBrand.VIVO -> R.string.optimization_desc_vivo
            DeviceBrand.REALME -> R.string.optimization_desc_realme
            else -> 0
        }
        return if (resourceId != 0) context.getString(resourceId) else ""
    }
}
