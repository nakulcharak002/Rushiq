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
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.example.rushiq.ui.theme.RushiqTheme
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.valentinilk.shimmer.ShimmerBounds

class MainActivity : ComponentActivity() {
    private lateinit var cartViewModel: CartViewModel
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
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
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
            Log.e("Error checking" , "auth status :${e.message}")
        }
    }

    private fun checkoutAuthStatusBeforeUI() {
        Log.d("MainActivity", "Checking auth status at activity level")

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Log the current state
        if (user != null) {
            Log.d("MainActivity", "User authenticated at activity level: ${user.email}")
        } else {
            Log.d("MainActivity", "No user authenticated at activity level")
        }

        // Optional: Clear any preferences that might be causing issues
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasToken = prefs.contains("user_id")

        if (hasToken) {
            Log.d("MainActivity", "Found stale token in preferences, clearing it")
            prefs.edit().remove("user_id").apply()
        }
    }


    private fun clearStaleToken() {
        // Help clean up any Razorpay resources
        try {
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val hasToken = prefs.contains("user_id")
            if (hasToken) {
                Log.d(TAG, "found take token in preference , clearing it ")
                prefs.edit().remove("user_id").apply()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning preferences: ${e.message}")
        }

    }
}

