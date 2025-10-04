// Top-level build.gradle.kts

plugins {
    kotlin("jvm") version "1.9.10" apply false
    kotlin("plugin.serialization") version "1.9.10" apply false
    id("com.android.application") version "8.3.0" apply false
    id("com.android.library") version "8.3.0" apply false

    // Keep integration branch aliases if using version catalog
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

