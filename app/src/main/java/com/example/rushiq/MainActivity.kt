package com.example.rushiq

import RushiqApp
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.example.rushiq.ui.theme.RushiqTheme
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val cartViewModel: CartViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAppComponents()
        setUpDisplaySettings()

        setContent {
            RushiqTheme {
                RushiqApp()
            }
        }
    }

    private fun initializeAppComponents() {
        checkoutAuthStatusBeforeUI()

        // REMOVED: Don't manually create ViewModel
        // cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        // The cartViewModel is now available through Hilt injection
        Log.d("MainActivity", "CartViewModel initialized with ${cartViewModel.getTotalItems()} items")
    }

    private fun setUpDisplaySettings() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            optimizeRefreshRate()
    }

    private fun optimizeRefreshRate() {
        try {
            window.attributes.preferredDisplayModeId =
                window.decorView.display?.supportedModes?.filter { it.refreshRate >= 120f }
                    ?.maxByOrNull { it.refreshRate }?.modeId ?: 0
        } catch (e: Exception) {
            Log.e("Error checking", "auth status: ${e.message}")
        }
    }

    private fun checkoutAuthStatusBeforeUI() {
        Log.d("MainActivity", "Checking auth status at activity level")

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            Log.d("MainActivity", "User authenticated at activity level: ${user.email}")
        } else {
            Log.d("MainActivity", "No user authenticated at activity level")
        }

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasToken = prefs.contains("user_id")

        if (hasToken) {
            Log.d("MainActivity", "Found stale token in preferences, clearing it")
            prefs.edit().remove("user_id").apply()
        }
    }

    private fun clearStaleToken() {
        try {
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val hasToken = prefs.contains("user_id")
            if (hasToken) {
                Log.d(TAG, "found stale token in preference, clearing it")
                prefs.edit().remove("user_id").apply()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning preferences: ${e.message}")
        }
    }
}
