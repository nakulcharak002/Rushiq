package com.example.rushiq.data.repository

import com.example.rushiq.data.api.MealApiService
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.data.models.mealDB.MealCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
class MealRepository @Inject constructor(
    private val api: MealApiService
) {

    fun getCategories(): Flow<Result<List<MealCategory>>> = flow {
        try {
            val response = api.getCategories()
            val categories = response.categories?.map { it.toCategory() } ?: emptyList()
            emit(Result.success(categories))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getMealsByCategory(category: String): Flow<Result<List<Products>>> = flow {
        try {
            val response = api.getMealsByCategory(category)
            val products = response.meals?.map { it.toProducts() } ?: emptyList()
            emit(Result.success(products))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun searchMeals(query: String): Flow<Result<List<Products>>> = flow {
        try {
            val response = api.searchMeals(query)
            val products = response.meals?.map { it.toProducts() } ?: emptyList()
            emit(Result.success(products))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getRandomMeals(count: Int = 50): Flow<Result<List<Products>>> = flow {
        try {
            val meals = mutableListOf<Products>()

            // Make multiple calls to get random meals
            repeat(count) {
                try {
                    val response = api.getRandomMeal()
                    response.meals?.firstOrNull()?.let { mealDto ->
                        meals.add(mealDto.toProducts())
                    }
                } catch (e: Exception) {
                    // Continue if one call fails
                }
            }

            emit(Result.success(meals))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}