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
    namespace = "com.example.zepto"
    compileSdk = 35

    defaultConfig {
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "PAYMENT_API_KEY", "\"$paymentapiKey\"")
        applicationId = "com.example.zepto"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        kapt {
            correctErrorTypes = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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

    // Add this configuration to resolve duplicate class issues
    configurations.all {
        resolutionStrategy {
            // Force a specific version for the gotrue-kt-android library
            force("io.github.jan-tennert.supabase:gotrue-kt-android:1.3.0")
            // Exclude the debug variant that's causing duplicates
            exclude(group = "io.github.jan-tennert.supabase", module = "gotrue-kt-android-debug")
        }
    }
}

dependencies {
    implementation("androidx.compose.runtime:runtime")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.appcheck.ktx)
    implementation(libs.volley)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Media3 ExoPlayer dependencies
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation(libs.androidx.constraintlayout.compose)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-gif:2.5.0")

    // Navigation Compose - the main navigation library
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Dagger Hilt core dependencies
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    // Hilt for Jetpack components
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Lifecycle components with Hilt integration - use consistent versions
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Location services
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    // Supabase - fixed dependencies to avoid conflicts
    implementation("io.github.jan-tennert.supabase:supabase-kt:1.3.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.3.0") {
        // Exclude the transitive dependency that might be causing conflicts
        exclude(group = "io.github.jan-tennert.supabase", module = "gotrue-kt-android-debug")
    }
    implementation("io.github.jan-tennert.supabase:gotrue-kt-android:1.3.0") {
        // Ensure we're only using the release variant
        exclude(group = "io.github.jan-tennert.supabase", module = "gotrue-kt-debug")
    }
//skydoves
    implementation ("com.github.skydoves:cloudy:0.1.2")
    // Required Ktor dependencies
    implementation("io.ktor:ktor-client-core:2.3.3")
    implementation("io.ktor:ktor-client-cio:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
// Razorpay
    implementation("com.razorpay:checkout:1.6.33")
    // Required serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("com.google.firebase:firebase-appcheck-safetynet:16.1.2")
    // logging interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // typography
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.4")
    //OSM
    implementation ("org.osmdroid:osmdroid-android:6.1.16")
    //shimmer effect
    implementation ("com.valentinilk.shimmer:compose-shimmer:1.0.5")
}

