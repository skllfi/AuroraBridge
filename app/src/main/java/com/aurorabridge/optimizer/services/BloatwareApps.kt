package com.aurorabridge.optimizer.services

object BloatwareApps {
    // A list of common bloatware package names
    val packageNames = setOf(
        "com.facebook.katana",
        "com.facebook.orca",
        "com.facebook.services",
        "com.facebook.system",
        "com.google.android.apps.tachyon", // Google Duo
        "com.google.android.videos", // Google Play Movies & TV
        "com.google.android.apps.youtube.music",
        "com.samsung.android.bixby.agent",
        "com.samsung.android.bixby.wakeup",
        "com.samsung.android.app.spage", // Samsung Daily
        "com.samsung.android.samsungpass",
        "com.samsung.android.samsungpassautofill",
        "com.samsung.android.svoiceime"
        // Add more packages as needed
    )
}
