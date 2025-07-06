package com.example.rushiq.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.rushiq.data.models.fakeapi.CartItem
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.ui.theme.utils.CartJsonAdapters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CartRepository {

    companion object {
        private const val PREF_NAME = "zepto_cart_preferences"
        private const val CART_ITEMS_KEY = "cart_items"
        private const val TAG = "PersistentCartRepository"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val gson: Gson = CartJsonAdapters.createGson()
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    init {
        loadCartItems()
    }

    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()

    override fun getTotalItem(): Flow<Int> =
        _cartItems.map { items -> items.sumOf { it.quantity } }

    override fun getTotalPrice(): Flow<Double> =
        _cartItems.map { items -> items.sumOf { it.products.price * it.quantity } }

    override suspend fun addToCart(products: Products) {
        _cartItems.update { currentCart ->
            val existingItem = currentCart.find { it.products.id == products.id }
            val updatedCart = if (existingItem != null) {
                currentCart.map { cartItem ->
                    if (cartItem.products.id == products.id) {
                        cartItem.copy(quantity = cartItem.quantity + 1)
                    } else cartItem
                }
            } else {
                currentCart + CartItem(products, 1)
            }
            saveCartItems(updatedCart)
            updatedCart
        }
    }

    override suspend fun removeFromCart(products: Products) {
        _cartItems.update { currentCart ->
            val existingItem = currentCart.find { it.products.id == products.id }
            val updatedCart = if (existingItem != null && existingItem.quantity > 1) {
                currentCart.map { cartItem ->
                    if (cartItem.products.id == products.id) {
                        cartItem.copy(quantity = cartItem.quantity - 1)
                    } else cartItem
                }
            } else {
                currentCart.filter { it.products.id != products.id }
            }
            saveCartItems(updatedCart)
            updatedCart
        }
    }

    override suspend fun setQuantity(product: Products, quantity: Int) {
        _cartItems.update { currentCart ->
            val updatedCart = if (quantity <= 0) {
                currentCart.filter { it.products.id != product.id }
            } else {
                val existingItem = currentCart.find { it.products.id == product.id }
                if (existingItem != null) {
                    currentCart.map { cartItem ->
                        if (cartItem.products.id == product.id) {
                            cartItem.copy(quantity = quantity)
                        } else cartItem
                    }
                } else {
                    currentCart + CartItem(product, quantity)
                }
            }
            saveCartItems(updatedCart)
            updatedCart
        }
    }

    override suspend fun getQuantity(productId: Int): Int =
        _cartItems.value.find { it.products.id == productId }?.quantity ?: 0

    override suspend fun clearCart() {
        _cartItems.value = emptyList()
        saveCartItems(emptyList())
    }

    private fun saveCartItems(items: List<CartItem>) {
        try {
            val json = gson.toJson(items)
            sharedPreferences.edit().putString(CART_ITEMS_KEY, json).apply()
            Log.d(TAG, "Cart saved to SharedPreferences, items: ${items.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving cart to SharedPreferences", e)
        }
    }

    private fun loadCartItems() {
        try {
            val json = sharedPreferences.getString(CART_ITEMS_KEY, null)
            if (!json.isNullOrEmpty()) {
                val type = object : TypeToken<List<CartItem>>() {}.type
                val loadedItems: List<CartItem> = gson.fromJson(json, type)
                _cartItems.value = loadedItems
                Log.d(TAG, "Cart loaded from SharedPreferences, items: ${loadedItems.size}")
            } else {
                Log.d(TAG, "No cart items found in SharedPreferences")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading cart from SharedPreferences", e)
            _cartItems.value = emptyList()
        }
    }
}
