package com.example.rushiq.ui.theme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rushiq.data.models.mealDB.PaymentRecord
import com.example.rushiq.data.repository.PaymentRepository
import com.example.rushiq.ui.theme.utils.PaymentStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.utils.EmptyContent.status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val TAG = "PaymentViewModel"

    init {
        Log.d(TAG, "Initializing PaymentViewModel")
    }

    // Using a nullable FirebaseFirestore instance to handle the case where FirebaseFirestore isn't properly set up
    private val firestore: FirebaseFirestore? = try {
        Log.d(TAG, "Attempting to initialize FirebaseFirestore instance")
        FirebaseFirestore.getInstance().also {
            Log.d(TAG, "FirebaseFirestore instance initialized successfully")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error initializing Firestore", e)
        e.printStackTrace()
        null
    }

    // Payment information
    private val _amount = MutableStateFlow(value = 0.0)
    val amount: StateFlow<Double> = _amount

    private val _orderId = MutableStateFlow(value = "")
    val orderId: StateFlow<String> = _orderId

    private val _userEmail = MutableStateFlow(value = "")
    val userEmail: StateFlow<String> = _userEmail

    private val _userPhone = MutableStateFlow(value = "")
    val userPhone: StateFlow<String> = _userPhone

    // Item Image URLs storage
    private val _itemImageUrls = MutableStateFlow<Map<String, String>>(emptyMap())
    val itemImageUrls: StateFlow<Map<String, String>> = _itemImageUrls

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _paymentStatus = MutableStateFlow<PaymentStatus>(PaymentStatus.Idle)
    val paymentStatus:StateFlow<PaymentStatus> = _paymentStatus
    private var navController: NavController?= null

    /**
     * Sets the NavController for navigation
     */
    fun setNavController(controller: NavController) {
        navController = controller
    }

    /**
     * Gets the NavController if available
     */
    fun getNavController(): NavController? {
        return navController
    }

    /**
     * Sets the user's email address
     */
    fun setUserEmail(email: String) {
        _userEmail.value = email
    }

    /**
     * Sets the user's phone number
     */
    fun setUserPhone(phone: String) {
        _userPhone.value = phone
    }

    /**
     * Sets the payment amount
     */
    fun setAmount(amount: Double) {
        _amount.value = amount
    }

    /**
     * Sets the order ID
     */
    fun setOrderId(id: String) {
        _orderId.value = id

    }
    /**
     * Sets the item image url
     */
    fun setItemImageUrls(imageUrls: Map<String, String>){
        Log.d(TAG, "set item image url : ${imageUrls.size}entries")
        _itemImageUrls.value= imageUrls
    }

    fun addItemImageUrl(itemId: String, imageUrl: String) {
        Log.d(TAG,  "Adding image URL for item $itemId: $imageUrl")
        val currentMap = _itemImageUrls.value.toMutableMap()
        currentMap[itemId] = imageUrl
        _itemImageUrls.value = currentMap
    }

    // Method to update payment status from outside
    fun updatePaymentStatus(status: PaymentStatus) {
        Log.d(TAG, "Payment status updated from: ${_paymentStatus.value} to: $status")
        _paymentStatus.value = status
    }

    fun setPaymentInfo(amount: Double, orderId: String, email: String, phone: String) {
        Log.d(TAG,  "Setting payment info - Amount: $amount, OrderId: $orderId, Email: $email, Phone: ${phone.take(6)}XXXX")
        _amount.value = amount
        _orderId.value = orderId
        _userEmail.value = email
        _userPhone.value = phone
        Log.d(TAG,  "Payment info set successfully")
    }

    fun savePaymentRecord(
        paymentId: String,
        orderId: String,
        amount: Double,
        items: List<String>,
        itemImageUrls: Map<String, String> = _itemImageUrls.value
    ): Job {
        // Use a different scope that won't be canceled when the ViewModel is cleared
        return CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG,  "Saving payment record, paymentId: $paymentId with ${itemImageUrls.size} image URLs")
                val result = paymentRepository.savePayment(
                    paymentId = paymentId,
                    orderId = orderId,
                    amount = amount,
                    itemCount = items.size,
                    items = items,
                    itemImageUrls = itemImageUrls
                ).also {

                    it.fold(
                        onSuccess = {
                            Log.d(TAG, "Payment record saved successfully")
                        },
                        onFailure = { e ->
                            Log.e(TAG, "Failed to save payment record", e)
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG,  "Exception while saving payment record", e)
            }
        }
    }

    fun getPaymentDetails(
        paymentId: String,
        onSuccess: (PaymentRecord) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Starting getPaymentDetails for paymentId: $paymentId")
            _isLoading.value = true
            try {
                // Try to get payment from repository first
                Log.d(TAG, "Attempting to get payment from repository")
                val repoResult = paymentRepository.getPayment(paymentId)

                if (repoResult.isSuccess) {
                    val payment = repoResult.getOrThrow()
                    Log.d(
                        TAG,
                        "Successfully retrieved payment from repository: ${payment.id}, amount: ${payment.amount}"
                    )

                    payment.itemImageUrls?.let {
                        if (it.isNotEmpty()) {
                            Log.d(TAG, "Retrieved payment has ${it.size} image URLs")
                        }
                    }

                    _isLoading.value = false
                    onSuccess(payment)
                    return@launch
                } else {
                    val exception = repoResult.exceptionOrNull()
                    Log.w(TAG, "Failed to get payment from repository", exception)
                }

                // Fallback to direct Firestore access
                Log.d(TAG, "Falling back to direct Firestore access")
                if (firestore == null) {
                    Log.e(TAG, "Firestore instance is null, cannot retrieve payment")
                    _isLoading.value = false
                    onError("Firestore is not available")
                    return@launch
                }
                try {
                    Log.d(TAG, "Querying Firestore collection 'payments' for document: $paymentId")
                    val document =
                        firestore.collection("payments").document(paymentId).get().await()

                    if (document.exists()) {
                        Log.d(TAG, "Document exists in Firestore")
                        val payment = document.toObject(PaymentRecord::class.java)

                        if (payment != null) {
                            Log.d(
                                TAG,
                                "Successfully converted document to PaymentRecord: ${payment.id}, amount: ${payment.amount}"
                            )
                            onSuccess(payment)
                        } else {
                            Log.d(
                                TAG,
                                "Document exists but conversion to PaymentRecord failed, creating dummy record"
                            )
                            createAndReturnDummyPayment(
                                paymentId,
                                "SUCCESS (Local - Conversion Failed)",
                                onSuccess
                            )
                        }
                    } else {
                        Log.d(TAG, "Document does not exist in Firestore, creating dummy record")
                        createAndReturnDummyPayment(
                            paymentId,
                            "SUCCESS (Local - Not Found)",
                            onSuccess
                        )
                    }
                } catch (e: FirebaseFirestoreException) {
                    Log.e(TAG, "FirebaseFirestoreException in getPaymentDetails", e)
                    e.printStackTrace()
                    createAndReturnDummyPayment(
                        paymentId,
                        "SUCCESS (Local - Firestore Error)",
                        onSuccess
                    )
                }

                Log.d(TAG, "Setting loading state to false")
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "General exception in getPaymentDetails", e)
                e.printStackTrace()

                _isLoading.value = false

                val errorMessage = e.message ?: "Unknown error occurred"
                Log.e(TAG, "Calling onError callback with: $errorMessage")
                onError(errorMessage)
            }
        }
    }




    }
