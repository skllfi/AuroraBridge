plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    // АКТИВИРУЕМ ПЛАГИН GOOGLE SERVICES
    id("com.google.gms.google-services")
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
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // FIREBASE BILL OF MATERIALS (BOM)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // ПЕРВАЯ ЗАВИСИМОСТЬ FIREBASE
    implementation("com.google.firebase:firebase-analytics")

    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}