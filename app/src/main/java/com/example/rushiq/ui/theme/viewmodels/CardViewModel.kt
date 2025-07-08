package com.example.rushiq.ui.theme.viewmodels

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.models.fakeapi.CartItem
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.data.repository.CartRepository
import com.example.zepto.data.repository.PaymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CartViewModel(
    private val cartRepository: CartRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    private val _tipAmount = MutableStateFlow(0.0)
    val tipAmount: StateFlow<Double> = _tipAmount

    private val _totalWithTip = MutableStateFlow(0.0)
    val totalWithTip: StateFlow<Double> = _totalWithTip

    private val _isFreeDeliveryApplied = MutableStateFlow(false)
    val isFreeDeliveryApplied: StateFlow<Boolean> = _isFreeDeliveryApplied

    private val _isApplyingFreeDelivery = MutableStateFlow(false)
    val isApplyingFreeDelivery: StateFlow<Boolean> = _isApplyingFreeDelivery

    private val _isBottomSheetVisible = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible

    private val _finalTotal = MutableStateFlow(0.0)
    val finalTotal: StateFlow<Double> = _finalTotal

    private val MIN_CART_VALUE_FOR_FREE_DELIVERY = 200.0
    private val DELIVERY_FEE = 30.0

    init {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _cartItems.value = items
                updateCartTotals()
            }
        }
    }

    fun getTotalItems(): Int {
        return _totalItems.value
    }

    fun addToCart(products: Products) {
        viewModelScope.launch {
            cartRepository.addToCart(products)
        }
    }

    fun removeFromCart(products: Products) {
        viewModelScope.launch {
            cartRepository.removeFromCart(products)
        }
    }

    fun updateQuantity(products: Products, quantity: Int) {
        viewModelScope.launch {
            cartRepository.setQuantity(products, quantity)
        }
    }

    fun getQuantity(products: Products): Int {
        return _cartItems.value.find { it.products.id == products.id }?.quantity ?: 0
    }

    fun isInCart(products: Products): Boolean {
        return _cartItems.value.any { it.products.id == products.id }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun setTipAmount(amount: Int) {
        _tipAmount.value = amount.toDouble()
        updateTotalTip()
        updateFinalTotal()
    }

    fun getTipAmount(): Int {
        return _tipAmount.value.toInt()
    }

    fun showBottomSheet() {
        _isBottomSheetVisible.value = true
    }

    fun hideBottomSheet() {
        _isBottomSheetVisible.value = false
    }

    private fun updateTotalTip() {
        _totalWithTip.value = _totalPrice.value + _tipAmount.value
    }

    private fun updateFinalTotal() {
        val discountPrice = (_totalPrice.value * 0.9).roundToInt().toDouble()
        val withDelivery = if (_isFreeDeliveryApplied.value) {
            discountPrice
        } else {
            discountPrice + DELIVERY_FEE
        }
        _finalTotal.value = withDelivery + _tipAmount.value
    }

    fun calculateTotalPrice() {
        val itemCost = _totalPrice.value
        val handlingCost = 14.99
        val gstOnHandling = 2.48

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val isLateNight = currentHour > 23 || currentHour < 6

        val lateNightFee = if (isLateNight) 25.0 else 0.0
        val gstOnLateNight = if (isLateNight) 4.13 else 0.0

        val deliveryFee = if (_isFreeDeliveryApplied.value) 0.0 else 30.0

        val exactTotalItem =
            itemCost + handlingCost + gstOnHandling + (if (isLateNight) gstOnLateNight else 0.0)

        val totalWithAllFees = exactTotalItem + deliveryFee + _tipAmount.value + lateNightFee

        _finalTotal.value = totalWithAllFees
        _totalWithTip.value = totalWithAllFees
    }

    fun applyFreeDelivery() {
        _isApplyingFreeDelivery.value = true

        viewModelScope.launch {
            delay(800L)
            _isFreeDeliveryApplied.value = true
            _isApplyingFreeDelivery.value = false
            calculateTotalPrice()
        }
    }

    private fun updateCartTotals() {
        var total = 0.0
        var count = 0

        for (item in _cartItems.value) {
            total += item.products.price * item.quantity
            count += item.quantity
        }

        _totalPrice.value = total
        _totalItems.value = count

        calculateTotalPrice()
    }

    fun generateOrderId(): String {
        return "ORDER-" + System.currentTimeMillis()
    }
}
