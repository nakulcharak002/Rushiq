package com.example.rushiq

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.rushiq.ui.theme.RushiqTheme
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RushiqTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestScreen()
                }
            }
        }
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
    val prefs = getSharedPreferences(name = "app_prefs", Context.MODE_PRIVATE)
    val hasToken = prefs.contains("user_id")

    if (hasToken) {
        Log.d("MainActivity", "Found stale token in preferences, clearing it")
        prefs.edit().remove(key = "user_id").apply()
    }
}
}

override fun onDestroy() {
    // Help clean up any Razorpay resources
    try {
        Checkout.clearUserData(this)
    } catch (e: Exception) {
        Log.e(TAG, "Error cleaning up Razorpay: ${e.message}")
    }
    super.onDestroy()
}
}
