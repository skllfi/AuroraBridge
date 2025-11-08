package com.aurorabridge.optimizer.adb

object AdbProfiles {

    private val profiles = mapOf(
        "Disable Bloatware (Safe)" to listOf(
            "pm uninstall -k --user 0 com.facebook.katana",
            "pm uninstall -k --user 0 com.facebook.orca",
            "pm uninstall -k --user 0 com.google.android.apps.youtube.music"
        ),
        "Aggressive Performance" to listOf(
            "settings put global window_animation_scale 0",
            "settings put global transition_animation_scale 0",
            "settings put global animator_duration_scale 0"
        )
    )

    fun getProfileNames(): List<String> {
        return profiles.keys.toList()
    }

    fun getCommandsForProfile(profileName: String): List<String> {
        return profiles[profileName] ?: emptyList()
    }
}
