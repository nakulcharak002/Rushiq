package com.example.rushiq.data.repository

import android.util.Log
import com.example.rushiq.data.api.FakeStoreApiServices
import com.example.rushiq.data.models.fakeapi.Products
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProductRepository"

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiServices: FakeStoreApiServices
) : ProductRepository {

    override fun getProducts(): Flow<List<Products>> = flow {
        Log.d(TAG, "Getting all products")
        val products = apiServices.fetchProducts()
        Log.d(TAG, "Fetched ${products.size} products")
        emit(products)
    }.catch { e ->
        Log.e(TAG, "Error fetching products: ${e.message}", e)
        emit(emptyList())
    }

    override fun getProductsByCategory(category: String): Flow<List<Products>> = flow {
        Log.d(TAG, "Getting products for category: $category")
        val products = apiServices.fetchProductsByCategory(category)
        Log.d(TAG, "Fetched ${products.size} products for category: $category")
        emit(products)
    }.catch { e ->
        Log.e(TAG, "Error fetching products for category $category: ${e.message}", e)
        val fallbackProducts = try {
            val allProducts = apiServices.fetchProducts()
            allProducts.filter { it.category?.lowercase() == category.lowercase() }
        } catch (e2: Exception) {
            Log.e(TAG, "Fallback also failed: ${e2.message}", e2)
            emptyList()
        }
        emit(fallbackProducts)
    }

    override fun getCategories(): Flow<List<String>> = flow {
        val categories = apiServices.fetchCategories()
        emit(categories)
    }.catch { e ->
        Log.e(TAG, "Error fetching categories : ${e.message}", e)
        emit(emptyList())
    }
}

