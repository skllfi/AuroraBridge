package com.aurorabridge.optimizer.repository

import android.app.Application
import com.aurorabridge.optimizer.model.OptimizationProfile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(private val application: Application) {

    private val json = Json { ignoreUnknownKeys = true }

    fun getAvailableProfiles(): List<OptimizationProfile> {
        // For now, we'll just load the default profile.
        // In the future, we can scan the assets/profiles directory for all .json files.
        return listOf(loadDefaultProfile())
    }

    fun loadDefaultProfile(): OptimizationProfile {
        val jsonString = application.assets.open("profiles/default.json").bufferedReader().use { it.readText() }
        return json.decodeFromString(jsonString)
    }
}
