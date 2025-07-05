package com.example.rushiq.data.repository

import android.adservices.adid.AdId
import com.example.rushiq.data.models.fakeapi.CartItem
import com.example.rushiq.data.models.fakeapi.Products
import kotlinx.coroutines.flow.Flow

interface CardRepository  {
    fun getCardItems():Flow<List<CartItem>>
    fun getTotalItem():Flow<Int>
    fun getTotalPrice():Flow<Double>
    suspend fun addToCard(products: Products)
    suspend fun removeFromCard(products: Products)
    suspend fun setQuantity (products: Products , quantity:Int)
    suspend fun getQuantity (productId: Int ):Int
    suspend fun clearCard()







}