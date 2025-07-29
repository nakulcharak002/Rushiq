package com.example.rushiq.data.repository

import android.bluetooth.BluetoothAssignedNumbers.GOOGLE
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val googleSignInOptions: GoogleSignInOptions,
    @ApplicationContext private val context: Context // Add @ApplicationContext annotation
) {
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    //custom users collection in firestore
    private val usersCollection = firestore.collection("users")

    init {
        if (auth.currentUser == null) {
            clearStoredToken()
        }
    }

    suspend fun signUpWithCustomEmail(email: String, password: String): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            val existingUsers = usersCollection.whereEqualTo("email", email).get().await()
            if (!existingUsers.isEmpty) {
                throw Exception("User with this email already exists")
            }
            // hash the password (use a proper hashing library in production)

            val hashedPassword = hashPassword(password)
            val userId = usersCollection.document().id
            val user = hashMapOf(
                "userId" to userId,
                "email" to email,
                "password" to hashedPassword,
                "createdAt" to System.currentTimeMillis()
            )
            usersCollection.document(userId).set(user).await()
            saveToken(userId)
            "Custom registration Successful"
        }
    }

    suspend fun signInWithCustomEmail(email: String, password: String): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            val querySnapshot = usersCollection.whereEqualTo("email", email)
                .get()
                .await()
            if (querySnapshot.isEmpty) {
                throw Exception("User Not found")
            }
            val userDoc = querySnapshot.documents.first()
            val storedPassword = userDoc.getString("password")
            if (storedPassword != hashPassword(password)) {
                throw Exception("invalid password")
            }
            // Save user session
            val userId = userDoc.getString("userId") ?: throw Exception("invalid user data")
            saveToken(userId)
            "Custom Login successful"
        }
    }

    fun getGoogleSignInIntent(): Intent {
        Log.d(TAG, "Creating Google Sign-In intent")
        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        googleSignInClient.signOut()
        return googleSignInClient.signInIntent
    }

    suspend fun handleGoogleSignInResult(data: Intent?): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            if (data == null) {
                Log.e(TAG, "No data received from Google Sign-In")
                throw Exception("No data received from Google Sign-In")
            }

            Log.d(TAG, "Processing Google Sign-In result")

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                    ?: throw Exception("Google Sign-In failed: No account data returned")

                Log.d(TAG, "Google account retrieved: ${account.email}")

                val idToken = account.idToken
                if (idToken == null) {
                    Log.e(TAG, "Google Authentication failed: No ID token")
                    throw Exception("Google Authentication failed: No ID token")
                }

                // Get credentials and sign in to Firebase
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                try {
                    val authResult = auth.signInWithCredential(credential).await()

                    authResult.user?.let { user ->
                        Log.d(TAG, "Successfully signed in with Google: ${user.email}")
                        saveToken(user.uid)

                        // Always save/update user information in Firestore
                        Log.d(TAG, "Saving/updating user in Firestore collection")

                        val userMap = hashMapOf(
                            "userId" to user.uid,
                            "email" to user.email,
                            "displayName" to user.displayName,
                            "photoUrl" to user.photoUrl?.toString(),
                            "providerType" to GOOGLE,
                            "lastLoginAt" to System.currentTimeMillis()
                        )

                        // Check if this is a new user
                        val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                        if (isNewUser) {
                            userMap["createdAt"] = System.currentTimeMillis()
                        }

                        // Use set with merge option to update existing document or create new user
                        usersCollection.document(user.uid).set(userMap, SetOptions.merge()).await()
                        Log.d(TAG, "User data saved to Firestore")

                        "Google Sign-In successful"
                    } ?: throw Exception("Failed to sign in with Google: No user returned")

                } catch (e: Exception) {
                    Log.e(TAG, "Firebase auth with Google credential failed", e)
                    throw Exception("Firebase authentication failed: ${e.message}")
                }

            } catch (e: ApiException) {
                val statusCode = e.statusCode
                Log.e(TAG, "Google Sign-In API Exception: code $statusCode, ${e.message}", e)
                throw Exception("Google Sign-In failed (code $statusCode): ${e.message ?: "API error"}")

            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In Exception: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun signOut(): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Signing out user")

            try {
                // Sign out from Firebase Auth
                auth.signOut()

                // Get Google Sign-In client and sign out from Google as well
                val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
                googleSignInClient.signOut().await()

                // Clear local storage
                clearStoredToken()
                storedVerificationId = null
                resendToken = null

                Log.d(TAG, "Successfully signed out")
                "Logged out successfully!"
            } catch (e: Exception) {
                Log.e(TAG, "Error during sign out", e)

                // Even if there's an error, try to clean up local state
                try {
                    clearStoredToken()
                    storedVerificationId = null
                    resendToken = null
                } catch (cleanupEx: Exception) {
                    Log.d(TAG, "Error during cleanup after failed sign out", cleanupEx)
                }

                throw e
            }
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun isAuthenticated(): Boolean {
        Log.d(TAG, "Checking authentication status")

        // First check Firebase current user
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            Log.d(TAG, "User authenticated via Firebase: ${firebaseUser.email}")
            // Also need to save the token to our local storage too for consistency
            saveToken(firebaseUser.uid)
            return true
        }

        // Then check local storage for custom auth
        val userId = sharedPreferences.getString("user_id", null)
        val isAuthViaCustom = !userId.isNullOrEmpty()

        if (isAuthViaCustom) {
            Log.d(TAG, "User authenticated via custom auth: $userId")
        } else {
            Log.d(TAG, "User is not authenticated")
            // Ensure we clear any stale tokens
            clearStoredToken()
        }

        return isAuthViaCustom
    }

    // Additional utility methods for easier usage
    fun getUserIdFromPreferences(): String? = sharedPreferences.getString("user_id", null)

    fun savePreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getPreference(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun saveBooleanPreference(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBooleanPreference(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString("user_id", token).apply()
    }

    private fun clearStoredToken() {
        sharedPreferences.edit().remove("user_id").apply()
    }
}