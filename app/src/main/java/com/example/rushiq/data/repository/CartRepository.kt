package com.example.rushiq.data.repository

import com.example.rushiq.data.models.fakeapi.CartItem
import com.example.rushiq.data.models.fakeapi.Products
import kotlinx.coroutines.flow.Flow

interface CartRepository  {
    fun getCartItems():Flow<List<CartItem>>
    fun getTotalItem():Flow<Int>
    fun getTotalPrice():Flow<Double>
    suspend fun addToCart(products: Products)
    suspend fun removeFromCart(products: Products)
    suspend fun setQuantity (products: Products , quantity:Int)
    suspend fun getQuantity (productId: Int ):Int
    suspend fun clearCart()







}