package com.example.rushiq.data.repository

import com.example.rushiq.data.models.fakeapi.Products
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Products>>
    fun getProductsByCategory(category: String) : Flow<List<Products>>
    fun getCategories(): Flow<List<String>>
}