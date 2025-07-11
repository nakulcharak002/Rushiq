package com.example.rushiq.data.models.mealDB

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class PaymentRecord(
    @DocumentId
    val id: String = "", // Razorpay payment ID
    val orderId: String = "",
    val amount: Double = 0.0,
    val timestamp: Date = Date(),
    val userEmail: String = "",
    val userPhone: String = "",
    val status: String = "", // SUCCESS, FAILED, etc.
    val userId: String = "", // Firebase Auth user ID
    val deliveryAddress: String = "",
    val items: List<String> = emptyList(), // List of item IDs or names
    val itemCount: Int = 0,
    val paymentMethod: String = "Razorpay",
    val metadata: Map<String, Any> = emptyMap(),
    val itemImageUrls: Map<String, String> = emptyMap() // Any additional data
)
