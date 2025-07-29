import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "1.8.21"
}

val localProperties =
    File(rootProject.rootDir, "local.properties").reader().use {
        Properties().apply { load(it) }
    }
val apiKey = localProperties.getProperty("API_KEY", "")
val paymentapiKey = localProperties.getProperty("PAYMENT_API_KEY", "")

android {
    namespace = "com.example.rushiq"
    compileSdk = 35

    defaultConfig {
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "PAYMENT_API_KEY", "\"$paymentapiKey\"")

        // FIX: Match applicationId with namespace
        applicationId = "com.example.rushiq"

        // FIX: Lower minSdk for better device compatibility
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // FIX: Move kapt configuration outside defaultConfig
    kapt {
        correctErrorTypes = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // FIX: Disable for debugging
            isShrinkResources = false  // FIX: Disable for debugging
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Resolve duplicate class issues
    configurations.all {
        resolutionStrategy {
            force("io.github.jan-tennert.supabase:gotrue-kt-android:1.3.0")
            exclude(group = "io.github.jan-tennert.supabase", module = "gotrue-kt-android-debug")
        }
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM - this manages all compose versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Remove duplicate runtime dependency
    // implementation("androidx.compose.runtime:runtime") // REMOVED - included in BOM

    // Essential Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Lifecycle with Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-gif:2.5.0")

    // Constraint layout for compose
    implementation(libs.androidx.constraintlayout.compose)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Other existing dependencies
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.appcheck.ktx)
    implementation(libs.volley)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Media3 ExoPlayer dependencies
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")

    // Fonts and UI
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.4")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Location services
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    // Supabase
    implementation("io.github.jan-tennert.supabase:supabase-kt:1.3.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.3.0") {
        exclude(group = "io.github.jan-tennert.supabase", module = "gotrue-kt-android-debug")
    }
    implementation("io.github.jan-tennert.supabase:gotrue-kt-android:1.3.0") {
        exclude(group = "io.github.jan-tennert.supabase", module = "gotrue-kt-debug")
    }

    // Ktor for networking
    implementation("io.ktor:ktor-client-core:2.3.3")
    implementation("io.ktor:ktor-client-cio:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")

    // Payment
    implementation("com.razorpay:checkout:1.6.33")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Firebase
    implementation("com.google.firebase:firebase-appcheck-safetynet:16.1.2")

    // UI Effects
    implementation("com.github.skydoves:cloudy:0.1.2")
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation("com.valentinilk.shimmer:compose-shimmer:1.0.5")
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    apply(plugin = "com.google.gms.google-services")
}