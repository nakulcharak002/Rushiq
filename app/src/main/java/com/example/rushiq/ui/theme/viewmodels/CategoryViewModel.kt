package com.example.rushiq.ui.theme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.models.fakeapi.Category
import com.example.rushiq.data.models.fakeapi.CategoryUiState
import com.example.rushiq.data.repository.CategoryRepository
import com.example.rushiq.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CategoryViewModel"

class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryId: String) {
        Log.d(TAG, "Loading category: $categoryId")
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    categoryId = categoryId
                )
            }

            try {
                val category = categoryRepository.getCategoryByIdOrName(categoryId)
                Log.d(TAG, "Category lookup result: $category")

                if (category != null) {
                    try {
                        val products = productRepository.getProductsByCategory(category.name).first()
                        Log.d(TAG, "Fetched ${products.size} products for category: ${category.name}")

                        if (products.isEmpty()) {
                            val directProducts = productRepository.getProductsByCategory(categoryId).first()
                            Log.d(TAG, "Fallback: direct fetch with categoryId got ${directProducts.size} products")

                            if (directProducts.isNotEmpty()) {
                                _uiState.update {
                                    it.copy(
                                        categoryName = formatCategoryName(category.name),
                                        products = directProducts,
                                        isLoading = false
                                    )
                                }
                                return@launch
                            }

                            val allProducts = productRepository.getProducts().first()
                            val filteredProducts = allProducts.filter { product ->
                                product.category?.lowercase() == category.name.lowercase() ||
                                        product.category?.lowercase() == categoryId.lowercase()
                            }

                            Log.d(TAG, "Filtered ${filteredProducts.size} products from all")

                            _uiState.update {
                                it.copy(
                                    categoryName = formatCategoryName(category.name),
                                    products = filteredProducts,
                                    isLoading = false
                                )
                            }

                        } else {
                            _uiState.update {
                                it.copy(
                                    categoryName = formatCategoryName(category.name),
                                    products = products,
                                    isLoading = false
                                )
                            }
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading products for category: ${e.message}", e)
                        _uiState.update {
                            it.copy(
                                error = "Error Loading Products: ${e.message}",
                                isLoading = false
                            )
                        }
                    }

                } else {
                    Log.d(TAG, "Category not found, trying direct fetch with ID: $categoryId")
                    try {
                        val directProducts = productRepository.getProductsByCategory(categoryId).first()
                        Log.d(TAG, "Direct fetch by ID got ${directProducts.size} products")

                        if (directProducts.isNotEmpty()) {
                            _uiState.update {
                                it.copy(
                                    categoryName = categoryId,
                                    products = directProducts,
                                    isLoading = false
                                )
                            }
                        } else {
                            val allProducts = productRepository.getProducts().first()
                            val filteredProducts = allProducts.filter { product ->
                                product.category?.lowercase() == categoryId.lowercase()
                            }

                            Log.d(TAG, "Filtered ${filteredProducts.size} products from all for $categoryId")

                            if (filteredProducts.isNotEmpty()) {
                                _uiState.update {
                                    it.copy(
                                        categoryName = formatCategoryName(categoryId),
                                        products = filteredProducts,
                                        isLoading = false
                                    )
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        categoryName = formatCategoryName(categoryId),
                                        error = "No Product found for the category",
                                        isLoading = false
                                    )
                                }
                            }
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Category not found and direct fetch failed: ${e.message}", e)
                        _uiState.update {
                            it.copy(
                                categoryName = formatCategoryName(categoryId),
                                error = "Category not found",
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading category: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        error = "Error loading category: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun formatCategoryName(category: String): String {
        return when (category.lowercase()) {
            "electronics" -> "Electronics"
            "jewelery" -> "Jewelry"
            "men's clothing" -> "Men's fashion"
            "women's clothing" -> "Women's fashion"
            else -> category.split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
    }
}
