package com.example.rushiq.ui.theme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.data.models.mealDB.MealCategory
import com.example.rushiq.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val TAG = "CafeViewModel"

    private val _categories = MutableStateFlow<List<MealCategory>>(emptyList())
    val categories: StateFlow<List<MealCategory>> = _categories

    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> = _products

    private val _selectedCategory = MutableStateFlow<MealCategory?>(null)
    val selectedCategory: StateFlow<MealCategory?> = _selectedCategory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Flag to track if initial data is loaded
    private var initialDataLoaded = false

    // Cache of all meals loaded
    private val allMeals = mutableListOf<Products>()

    init {
        Log.d(TAG, "CafeViewModel initialized")
        // Set loading to true immediately
        _isLoading.value = true

        // Load categories and initial meals in parallel
        viewModelScope.launch {
            try {
                launch { loadCategories() } // Fixed: Added parentheses
                launch { loadInitialMeals() } // Fixed: Added parentheses
            } catch (e: Exception) {
                Log.e(TAG, "Error during initialization", e)
                _error.value = e.message ?: "Failed to initialize"
                _isLoading.value = false
            }
        }
    }

    private fun loadCategories() {
        Log.d(TAG, "Loading categories")
        viewModelScope.launch {
            repository.getCategories()
                .onEach { result ->
                    result.fold(
                        onSuccess = { categories ->
                            Log.d(TAG, "Successfully loaded ${categories.size} categories")

                            // Create "All" category to show all items
                            val allCategory = MealCategory("All", "All Meals", "")
                            val updatedCategories = listOf(allCategory) + categories
                            _categories.value = updatedCategories

                            // Auto-select "All" category
                            if (!initialDataLoaded) {
                                Log.d(TAG, "Auto-selecting All category")
                                _selectedCategory.value = allCategory
                            }
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to load categories", exception)
                            _error.value = exception.message ?: "Failed to load categories"
                        }
                    )
                    // Don't set isLoading to false here
                }.catch { e ->
                    Log.e(TAG, "Error collecting categories", e)
                    _error.value = e.message ?: "Unknown error occurred"
                }
                .launchIn(viewModelScope)
        }
    }

    private fun loadInitialMeals() {
        Log.d(TAG, "Loading initial meals")
        viewModelScope.launch {
            repository.getRandomMeals(50)
                .onEach { result ->
                    result.fold(
                        onSuccess = { products ->
                            Log.d(TAG, "Successfully loaded ${products.size} initial meals")
                            allMeals.clear()
                            allMeals.addAll(products)

                            // Show all meals
                            _products.value = allMeals
                            initialDataLoaded = true

                            // Only set loading to false when data is actually loaded
                            _isLoading.value = false
                        }, // Removed unnecessary type cast
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to load initial meals", exception)
                            _error.value = exception.message ?: "Failed to load initial meals"
                            _isLoading.value = false
                        }
                    )
                }
                .catch { e ->
                    Log.e(TAG, "Error while loading initial meals", e)
                    _error.value = e.message ?: "Unknown error occurred"
                    _isLoading.value = false
                }
                .launchIn(viewModelScope)
        }
    }
}