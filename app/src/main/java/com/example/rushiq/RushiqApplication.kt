package com.example.rushiq

// File: app/src/main/java/com/example/rushiq/RushiqApplication.kt
// IMPORTANT: Ensure this matches your project's root package

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp // Essential import for Hilt

/**
 * The custom Application class for the Rushiq app.
 *
 * This class is the first component of your app to be instantiated by the Android system
 * when your application process starts.
 *
 * The `@HiltAndroidApp` annotation is crucial here:
 * - It tells Dagger Hilt to generate the necessary code for application-level
 * dependency injection. This sets up the root component that Hilt uses to
 * provide dependencies throughout your entire app.
 * - Without this, Hilt will not be able to inject ViewModels (via `hiltViewModel()`)
 * or other dependencies into your `@AndroidEntryPoint` annotated classes
 * like `MainActivity`.
 */
@HiltAndroidApp // This annotation is the entry point for Hilt's code generation
class RushiqApplication : Application() {

    /**
     * Called when the application is first created.
     * This is the earliest point in your application's lifecycle where you can
     * perform global initializations.
     */
    override fun onCreate() {
        super.onCreate()
        // It's a good practice to log when your Application class is created,
        // especially during development, to confirm it's being initialized.
        Log.d("RushiqApplication", "RushiqApplication onCreate() called. Application is starting and Hilt is being initialized.")

        // --- Common things you might initialize here: ---

        // 1. Firebase Initialization (if you're using Firebase services like Auth, Firestore, etc.)
        //    If you use Google Services plugin, Firebase might auto-initialize, but explicit
        //    initialization here ensures it's done early.
        // import com.google.firebase.FirebaseApp
        // FirebaseApp.initializeApp(this)

        // 2. Third-party SDKs that require an Application Context for setup:
        //    e.g., analytics SDKs, crash reporting SDKs, payment gateway SDKs.
        // import com.example.some_sdk.AnalyticsSdk
        // AnalyticsSdk.init(this)

        // 3. Global configurations or singletons that need to be available app-wide:
        //    (Though for dependency injection, Hilt modules are often preferred for this)
        // AppConfig.load(this)
    }

    /**
     * Called by the system when the device configuration changes.
     * (e.g., orientation change, locale change).
     * You typically don't need to do much here for Compose apps as Compose handles
     * many configuration changes automatically.
     */
    // override fun onConfigurationChanged(newConfig: Configuration) {
    //     super.onConfigurationChanged(newConfig)
    //     Log.d("RushiqApplication", "onConfigurationChanged: ${newConfig}")
    // }

    /**
     * Called when the overall system is running low on memory.
     * You can use this to release large cached resources to help the system.
     */
    // override fun onLowMemory() {
    //     super.onLowMemory()
    //     Log.w("RushiqApplication", "onLowMemory: System is running low on memory. Releasing resources.")
    // }

    /**
     * Called when the application is about to be terminated.
     * NOTE: This method is NOT guaranteed to be called in all scenarios,
     * especially if the system kills your app process due to memory pressure.
     * Do not rely on this for critical cleanup or saving state.
     */
    // override fun onTerminate() {
    //     super.onTerminate()
    //     Log.d("RushiqApplication", "onTerminate: Application process is being terminated.")
    // }
}