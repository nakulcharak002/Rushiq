package com.example.rushiq.ui.theme.viewmodels
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.models.mealDB.PaymentRecord
import com.example.rushiq.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentDetailState {
    object Loading : PaymentDetailState()
    data class Success(val payment: PaymentRecord) : PaymentDetailState()
    data class Error(val message: String) : PaymentDetailState()
}

@HiltViewModel
class PaymentDetailViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val TAG = "PaymentDetailViewModel"

    private val _uiState = MutableStateFlow<PaymentDetailState>(PaymentDetailState.Loading)
    val uiState: StateFlow<PaymentDetailState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "PaymentDetailViewModel initialized")
    }

    /**
     * Load payment details for a specific payment ID
     */
    fun loadPaymentDetail(paymentId: String) {
        Log.d(TAG, "loadPaymentDetail() - Loading payment with ID: $paymentId")

        if (paymentId.isBlank()) {
            Log.e(TAG, "loadPaymentDetail() - Payment ID is blank")
            _uiState.value = PaymentDetailState.Error("Invalid payment ID")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = PaymentDetailState.Loading
                Log.d(TAG, "loadPaymentDetail() - Set state to Loading")

                val result = paymentRepository.getPayment(paymentId)

                result.fold(
                    onSuccess = { payment ->
                        Log.d(TAG, "loadPaymentDetail() - Successfully loaded payment")
                        Log.d(TAG, "Payment details - ID: ${payment.id}, Amount: ${payment.amount}, Status: ${payment.status}")
                        Log.d(TAG, "Payment has ${payment.items.size} items")

                        // Log image URLs if available
                        payment.itemImageUrls?.let { urls ->
                            Log.d(TAG, "Payment has ${urls.size} image URLs")
                            urls.forEach { (key, url) ->
                                Log.d(TAG, "Image URL - Key: '$key', URL: ${url.take(50)}...")
                            }
                        } ?: Log.d(TAG, "Payment has no image URLs")

                        _uiState.value = PaymentDetailState.Success(payment)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "loadPaymentDetail() - Failed to load payment", exception)
                        val errorMessage = when {
                            exception.message?.contains("User not logged in") == true ->
                                "Please log in to view payment details"
                            exception.message?.contains("Payment not found") == true ->
                                "Payment not found. It may have been deleted or you don't have access to it."
                            exception.message?.contains("network", ignoreCase = true) == true ->
                                "Network error. Please check your internet connection and try again."
                            else ->
                                "Unable to load payment details. Please try again."
                        }
                        _uiState.value = PaymentDetailState.Error(errorMessage)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "loadPaymentDetail() - Unexpected error", e)
                _uiState.value = PaymentDetailState.Error("An unexpected error occurred. Please try again.")
            }
        }
    }

    /**
     * Retry loading payment details
     */
    fun retryLoadPayment(paymentId: String) {
        Log.d(TAG, "retryLoadPayment() - Retrying payment load for ID: $paymentId")
        loadPaymentDetail(paymentId)
    }

    /**
     * Clear any error states
     */
    fun clearError() {
        Log.d(TAG, "clearError() - Clearing error state")
        if (_uiState.value is PaymentDetailState.Error) {
            _uiState.value = PaymentDetailState.Loading
        }
    }

    /**
     * Get current payment if available
     */
    fun getCurrentPayment(): PaymentRecord? {
        return when (val state = _uiState.value) {
            is PaymentDetailState.Success -> state.payment
            else -> null
        }
    }

    /**
     * Check if currently loading
     */
    fun isLoading(): Boolean {
        return _uiState.value is PaymentDetailState.Loading
    }

    /**
     * Check if there's an error
     */
    fun hasError(): Boolean {
        return _uiState.value is PaymentDetailState.Error
    }

    /**
     * Get error message if available
     */
    fun getErrorMessage(): String? {
        return when (val state = _uiState.value) {
            is PaymentDetailState.Error -> state.message
            else -> null
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "PaymentDetailViewModel cleared")
    }
}