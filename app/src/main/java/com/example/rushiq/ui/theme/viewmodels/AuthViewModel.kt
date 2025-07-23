package com.example.rushiq.ui.theme.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.repository.AuthRepository
import com.example.rushiq.ui.theme.utils.AuthState
import com.google.firebase.auth.PhoneAuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "AuthViewModel"
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAuthLoading = MutableStateFlow(true)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _phoneNumber = MutableStateFlow<String?>(null)
    val phoneNumber: StateFlow<String?> = _phoneNumber.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    // Store credential for auto-verification
    private var verificationCredential: PhoneAuthCredential? = null

    init {
        Log.d(TAG, "Initializing AuthViewModel")
        // Start with loading state
        _isAuthLoading.value = true
        // Check auth status immediately on init
        viewModelScope.launch {
            try {
                checkAuthStatus()
            } finally {
                // Regardless of result, mark loading as complete
                _isAuthLoading.value = false
            }
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            Log.d(TAG, "Checking authentication status")
            try {
                _isAuthLoading.value = true
                val authenticated = authRepository.isAuthenticated()
                Log.d(TAG, "Authentication check result: $authenticated")
                _isAuthenticated.value = authenticated

                // If not authenticated, ensure we're in a clean state
                if (!authenticated) {
                    _phoneNumber.value = null
                    _otpSent.value = false
                    verificationCredential = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking authentication status", e)
                _isAuthenticated.value = false
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    // Custom Email/Password authentication
    fun signUpWithCustomEmail(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "Attempting custom sign up with email: $email")
            _authState.value = AuthState.Loading
            val result = authRepository.signUpWithCustomEmail(email, password)
            processAuthResult(result, "Custom sign up")
        }
    }
    fun signInWithCustomEmail(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "Attempting custom sign up with email: $email")
            _authState.value = AuthState.Loading
            val result = authRepository.signUpWithCustomEmail(email, password)
            processAuthResult(result, "Custom sign up")
        }
    }
    //Google authentication
    fun getGoogleSignInIntent(activity: Activity):Intent{
        Log.d(TAG , "getting google sign - In intent")
        _authState.value = AuthState.Loading
        return authRepository.getGoogleSignInIntent()

    }


    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            Log.d(TAG, "Handling Google sign-in result")

            if (data == null) {
                Log.e(TAG, "Google sign-in data is null")
                _authState.value = AuthState.Error("Google sign-in cancelled or failed")
                return@launch
            }

            _authState.value = AuthState.Loading

            try {
                val result = authRepository.handleGoogleSignInResult(data)

                result.fold(
                    onSuccess = { message ->
                        Log.d(TAG, "Google sign-in successful: $message")
                        _authState.value = AuthState.Success(message)

                        // Critical: Force immediate auth status check
                        val authenticated = authRepository.isAuthenticated()
                        Log.d(TAG, "After Google sign-in, isAuthenticated: $authenticated")
                        _isAuthenticated.value = authenticated

                        if (!authenticated) {
                            Log.e(TAG, "Authentication succeeded but isAuthenticated is still false")
                            _authState.value = AuthState.Error(
                                "Authentication status inconsistent, please try again"
                            )
                        }
                    },
                    onFailure = { exception ->
                        val errorMsg = exception.localizedMessage ?: "Unknown error occurred"
                        Log.e(TAG, "Google sign-in failed: $errorMsg", exception)
                        _authState.value = AuthState.Error(errorMsg)
                        checkAuthStatus()
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception during Google sign-in", e)
                _authState.value = AuthState.Error("Google sign-in failed: ${e.message ?: "Unknown error"}")
                checkAuthStatus()
            }
        }
    }

    fun getCurrentUser() = authRepository.getCurrentUser()



    private fun processAuthResult(result: Result<String>, operation: String) {
        result.fold(
            onSuccess = { message ->
                Log.d(TAG,  "Successful $operation: $message")
                _authState.value = AuthState.Success(message)

                // IMPORTANT: Set isAuthenticated to true immediately
                _isAuthenticated.value = true

                // Additional check to verify authentication status as backup
                checkAuthStatus()
            },
            onFailure = { exception ->
                val errorMsg = exception.localizedMessage ?: "Unknown error occurred"
                Log.e(TAG,  "$operation failed: $errorMsg", exception)
                _authState.value = AuthState.Error(errorMsg)
            }
        )
    }
    fun signOut(){
        viewModelScope.launch {
                Log.d(TAG , "Attempting sign Out")
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.signOut()
                result.onSuccess {message ->
                    Log.d(TAG , "Sign out successful : $message")
                    _authState.value = AuthState.Success(message)
                    // important :Explicitly set authentication state to false immediately
                    _isAuthenticated.value = false
                    // clear session data
                    clearSessionData()

                }.onFailure { exception ->
                    val errorMsg = exception.localizedMessage?:"Sign Out failed"
                    Log.e(TAG , errorMsg , exception)
                    _authState.value = AuthState.Error(errorMsg)
                    checkAuthStatus()
                }
            }catch (e: Exception){
                Log.e(TAG , "exception during log out", e)
                _authState.value= AuthState.Error("sign Out failed:${e.message}")
                checkAuthStatus()
            }
        }
    }
    private fun clearSessionData(){
        _phoneNumber.value = null
        _otpSent.value = false
        verificationCredential = null
    }


    fun resetAuthState() {
        Log.d(TAG,  "Resetting auth state")
        _authState.value = AuthState.Initial
    }

    companion object {
        const val GOOGLE_SIGN_IN_REQUEST_CODE = 9001
    }
    fun setErrorState(message: String) {
        Log.d(TAG, "Setting error state: $message")
        _authState.value = AuthState.Error(message)
    }

}