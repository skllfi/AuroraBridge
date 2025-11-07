plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.aurorabridge.optimizer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aurorabridge.optimizer"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "0.2"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.0"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.03.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.0")
        implementation("androidx.navigation:navigation-compose:2.7.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
        // If using AutoStarter/AppKiller from JitPack, add maven { url = uri("https://jitpack.io") } to repositories
        // and uncomment or add the implementation lines below:
        // implementation("com.github.judemanutd:AutoStarter:1.0.8")
        // implementation("com.github.gitter-badger:AppKillerManager:1.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
