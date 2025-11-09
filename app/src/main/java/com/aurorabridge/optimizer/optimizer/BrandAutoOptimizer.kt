package com.aurorabridge.optimizer.optimizer

import android.content.Context
import android.os.Build
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.DeviceBrand
import com.aurorabridge.optimizer.utils.IAdbCommander
import org.json.JSONObject
import java_io.BufferedReader
import java_io.InputStreamReader

class BrandAutoOptimizer(
    private val context: Context,
    private val adbCommander: IAdbCommander
) {

    fun getProfileForCurrentDevice(): OptimizationProfile? {
        val brand = getDeviceBrand()
        if (brand == DeviceBrand.UNKNOWN) return null

        val commands = getOptimizationCommands(brand, context.packageName)
        val description = getOptimizationDescription(brand)

        return OptimizationProfile(
            brandName = brand.name.toLowerCase().capitalize(),
            description = description,
            commands = commands
        )
    }

    suspend fun applyOptimization(profile: OptimizationProfile): Boolean {
        var allSucceeded = true
        for (command in profile.commands) {
            val result = adbCommander.runAdbCommandAsync(command)
            if (!result.isSuccess) {
                allSucceeded = false
                break
            }
        }
        return allSucceeded
    }

    private fun getDeviceBrand(): DeviceBrand {
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

    private fun getOptimizationCommands(brand: DeviceBrand, packageName: String): List<String> {
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

    private fun getOptimizationDescription(brand: DeviceBrand): Int {
        return when (brand) {
            DeviceBrand.XIAOMI -> R.string.optimization_desc_xiaomi
            DeviceBrand.HUAWEI -> R.string.optimization_desc_huawei
            DeviceBrand.PIXEL -> R.string.optimization_desc_pixel
            DeviceBrand.SAMSUNG -> R.string.optimization_desc_samsung
            DeviceBrand.ONEPLUS -> R.string.optimization_desc_oneplus
            DeviceBrand.OPPO -> R.string.optimization_desc_oppo
            DeviceBrand.VIVO -> R.string.optimization_desc_vivo
            DeviceBrand.REALME -> R.string.optimization_desc_realme
            else -> R.string.empty
        }
    }
}
