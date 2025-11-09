pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AuroraBridgeDeviceOptimizer"
include(":app")

plugins {
    id("com.google.gms.google-services") version "4.4.3" apply false
}
