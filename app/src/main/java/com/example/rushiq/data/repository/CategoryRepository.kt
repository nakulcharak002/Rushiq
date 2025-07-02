package com.example.rushiq.data.repository

import kotlinx.coroutines.flow.Flow
import java.util.Locale.Category

interface CategoryRepository {
    fun getCategories(): Flow<List<com.example.rushiq.data.models.fakeapi.Category>>

    suspend fun getCategoryByIdOrName(idOrName : String ): com.example.rushiq.data.models.fakeapi.Category?

}