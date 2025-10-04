// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build.gradle.kts

plugins {
    kotlin("jvm") version "1.9.10" apply false
    kotlin("plugin.serialization") version "1.9.10" apply false
    id("com.android.application") version "8.3.0" apply false
    id("com.android.library") version "8.3.0" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Not strictly needed if using plugins {} block above
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
