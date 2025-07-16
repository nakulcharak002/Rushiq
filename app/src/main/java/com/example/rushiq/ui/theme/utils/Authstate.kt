package com.example.rushiq.ui.theme.utils

import com.google.firebase.auth.PhoneAuthCredential

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

// Phone authentication specific states
sealed class PhoneAuthState {
    data class CodeSent(val message: String) : PhoneAuthState()
    data class VerificationCompleted(val credential: PhoneAuthCredential) : PhoneAuthState()
    data class VerificationFailed(val message: String) : PhoneAuthState()
    object CodeAutoRetrievalTimeout : PhoneAuthState()
}