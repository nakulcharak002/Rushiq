package com.example.rushiq.ui.theme.utils

sealed class PaymentStatus {
    object Idle : PaymentStatus()
    object Processing : PaymentStatus()
    object Success : PaymentStatus()
    object SuccessButNotSynced : PaymentStatus()
    object FirestoreNotEnabled : PaymentStatus()
    data class Error(val message: String) : PaymentStatus()
}