package com.aurorabridge.optimizer.optimizer

import android.content.Context
import android.os.Build
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.adb.AdbOptimizer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

@Serializable
data class OptimizationProfile(
    val name: String,
    val manufacturer: String,
    val description: String,
    val commands: List<String>
)

class BrandAutoOptimizer(private val context: Context) {

    private val profiles: List<OptimizationProfile> by lazy {
        loadProfiles()
    }

    fun getProfileForCurrentDevice(): OptimizationProfile? {
        val manufacturer = Build.MANUFACTURER.toLowerCase()
        return profiles.find { it.manufacturer.equals(manufacturer, ignoreCase = true) }
    }

    fun applyOptimization(profileName: String): Map<String, String> {
        return AdbOptimizer.applyProfile(context, profileName)
    }

    private fun loadProfiles(): List<OptimizationProfile> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.optimization_profiles)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            Json.decodeFromString(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
