buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // This is the fix, adapted from Firebase docs for a known issue.
        // We forcefully add the performance plugin to the classpath with an exclusion
        // to resolve a dependency conflict (Guava).
        classpath("com.google.firebase:perf-plugin:1.4.2") {
            exclude(group = "com.google.guava", module = "guava-jdk5")
        }
    }
}

plugins {
    kotlin("android") version "1.9.21" apply false
    id("com.android.application") version "8.1.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21" apply false
}
