package com.example.rushiq.ui.theme.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.models.fakeapi.Products
import com.example.zepto.data.repository.CartRepository
import com.example.zepto.data.repository.PaymentRepository
import com.example.zepto.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {

    //  Cart Items List
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Total number of items
    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    // Total price of products
    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    // Tip amount added by user
    private val _tipAmount = MutableStateFlow(0.0)
    val tipAmount: StateFlow<Double> = _tipAmount

    // Total including tip
    private val _totalWithTip = MutableStateFlow(0.0)
    val totalWithTip: StateFlow<Double> = _totalWithTip

    // Free delivery flag
    private val _isFreeDeliveryApplied = MutableStateFlow(false)
    val isFreeDeliveryApplied: StateFlow<Boolean> = _isFreeDeliveryApplied

    // Applying free delivery status
    private val _isApplyingFreeDelivery = MutableStateFlow(false)
    val isApplyingFreeDelivery: StateFlow<Boolean> = _isApplyingFreeDelivery

    //  Bottom sheet visibility (e.g., tip / checkout)
    private val _isBottomSheetVisible = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible

    // Final Total (total + tip + delivery logic etc.)
    private val _finalTotal = MutableStateFlow(0.0)
    val finalTotal: StateFlow<Double> = _finalTotal

    fun addToCard(products: Products){
        viewModelScope.launch{
            cartRepository.addTocard(products)
        }
    }

}

