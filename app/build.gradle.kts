plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}


android {
    namespace = "com.emergency.mesh"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.emergency.mesh"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Compose
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Google Nearby Connections API
    implementation("com.google.android.gms:play-services-nearby:19.0.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Compose
    // Compose
    implementation("androidx.compose.ui:ui:1.5.2")
    implementation("androidx.compose.material:material:1.5.2") // optional if using material2 components
    implementation("androidx.compose.material3:material3:1.2.1") // add this
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1") // add this
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.2")



    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
