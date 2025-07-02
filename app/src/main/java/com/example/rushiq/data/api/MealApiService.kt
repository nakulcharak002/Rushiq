package com.example.rushiq.data.api

import com.example.rushiq.data.models.mealDB.CategoryResponse
import com.example.rushiq.data.models.mealDB.MealCategory
import com.example.rushiq.data.models.mealDB.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") searchQuery: String):MealResponse

    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("C") categoryName : String): MealResponse

    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse



}