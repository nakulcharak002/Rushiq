package com.example.rushiq.data.repository

import android.content.Context
import android.util.Log
import com.example.rushiq.data.models.mealDB.PaymentRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context  // Fixed: Use Context with @ApplicationContext annotation
) {
    private val TAG = "PaymentRepository"
    private val usersCollection = firestore.collection("users")
    private val ordersCollection = firestore.collection("orders")

    init {
        Log.i(TAG, "PaymentRepository initialized")
        Log.d(
            TAG,
            "Collections path - users:${usersCollection.path} , orders : ${ordersCollection.path}"
        )

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w(TAG, "No authenticated user found during initialization")
        } else {
            Log.w(TAG, "initialized with authenticated user : ${currentUser.uid.take(5)}... ")
        }
    }

    suspend fun savePayment(
        paymentId: String,
        orderId: String,
        amount: Double,
        itemCount: Int,
        items: List<String>,
        itemImageUrls: Map<String, String>
    ): Result<PaymentRecord> {

        Log.i(
            TAG,
            "savePayment() - Starting payment save - paymentId: $paymentId, orderId: $orderId, amount: $amount"
        )
        Log.d(TAG, "savePayment() - Item images count: ${itemImageUrls.size}")

        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "savePayment failed: User not logged in")
                return Result.failure(Exception("User not logged in"))
            }

            val userId = currentUser.uid
            val userEmail = currentUser.email ?: ""
            val userPhone = currentUser.phoneNumber ?: ""

            Log.d(
                TAG,
                "User info - userId: ${userId.take(5)}...," + "email: ${
                    if (userEmail.isBlank()) "blank" else "${
                        userEmail.substringBefore(
                            '@'
                        )
                    }@..."
                }"
            )

            // Create payment record object
            val paymentRecord = PaymentRecord(
                id = paymentId,
                orderId = orderId,
                amount = amount,
                timestamp = Date(),
                userEmail = userEmail,
                userPhone = userPhone,
                status = "SUCCESS",
                userId = userId,
                items = items,
                itemImageUrls = itemImageUrls
            )

            try {
                val userDoc = usersCollection.document(userId).get().await()
                if (!userDoc.exists()) {
                    Log.d(TAG, "Creating user document first")
                    val userData = mapOf(
                        "userId" to userId,
                        "email" to userEmail,
                        "phoneNumber" to userPhone,
                        "createdAt" to Date()
                    )
                    usersCollection.document(userId).set(userData).await()
                }

                // save payment to subCollection
                usersCollection.document(userId)
                    .collection("payments")
                    .document(paymentId)
                    .set(paymentRecord)
                    .await()

                //update user document with latest payment summary
                usersCollection.document(userId).update(
                    mapOf(
                        "lastPaymentId" to paymentId,
                        "lastPaymentAmount" to amount,
                        "lastPaymentDate" to Date(),
                    )
                ).await()

                Log.d(TAG, "Payment Successfully saved to the users/$userId/payments/$paymentId")
                Result.success(paymentRecord)

            } catch (e: Exception) {
                Log.e(TAG, "Error saving payment", e)
                e.printStackTrace()
                Result.failure(e)
            }

        } catch (e: Exception) {
            Log.e(TAG, "savePayment() failed", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getPayment(paymentId: String): Result<PaymentRecord> {
        Log.d(TAG, "getPayment() - Retrieving payment with ID: $paymentId")

        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "getPayment failed: User not logged in")
                return Result.failure(Exception("User not logged in"))
            }

            val userId = currentUser.uid
            Log.d(TAG, "Querying Firestore for document: users/$userId/payments/$paymentId")

            val document = usersCollection.document(userId)
                .collection("payments")
                .document(paymentId)
                .get()
                .await()

            if (document.exists()) {
                Log.d(TAG, "Document exists, attempting to convert to PaymentRecord")
                val payment = document.toObject(PaymentRecord::class.java)

                if (payment != null) {
                    Log.d(TAG, "Successfully retrieved payment - amount: ${payment.amount}, status: ${payment.status}")
                    // Log number of image URLs retrieved if any
                    payment.itemImageUrls?.let {
                        Log.d(TAG, "Retrieved payment has ${it.size} item image urls")
                    }
                    Result.success(payment)
                } else {
                    Log.e(TAG, "Document exists but conversion to PaymentRecord failed")
                    Result.failure(Exception("Payment conversion failed"))
                }
            } else {
                Log.e(TAG, "Payment document not found: users/$userId/payments/$paymentId")
                Result.failure(Exception("Payment not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving payment", e)
            Result.failure(e)
        }
    }

    /**
     * Get all payments for current user
     */
    suspend fun getUserPayments(): Result<List<PaymentRecord>> {
        Log.d(TAG, "getUserPayments() - Retrieving payments for current user")

        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "getUserPayments failed: User not logged in")
                return Result.failure(Exception("User not logged in"))
            }

            val userId = currentUser.uid
            Log.d(TAG, "Querying payments for userId: ${userId.take(5)}...")

            try {
                Log.d(TAG, "Executing Firestore query: users/$userId/payments collection")
                val querySnapshot = usersCollection.document(userId)
                    .collection("payments")
                    .orderBy("timestamp")
                    .get()
                    .await()

                Log.d(TAG, "Query returned ${querySnapshot.size()} documents")

                val payments = querySnapshot.documents.mapNotNull {
                    try {
                        it.toObject(PaymentRecord::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting document ${it.id} to PaymentRecord", e)
                        null
                    }
                }

                Log.d(TAG, "Successfully converted ${payments.size} payment records")
                Log.d(TAG, "getUserPayments() completed successfully with ${payments.size} payments")

                Result.success(payments)

            } catch (e: Exception) {
                Log.e(TAG, "Error querying user payments", e)
                e.printStackTrace()
                throw e
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in getUserPayments", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }
}