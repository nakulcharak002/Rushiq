package com.example.rushiq.di

import com.example.rushiq.data.api.FakeStoreApiServices
import com.example.rushiq.data.repository.CategoryRepository
import com.example.rushiq.data.repository.CategoryRepositoryImpl
import com.example.rushiq.data.repository.ProductRepository
import com.example.rushiq.data.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

// -------------------------
// 1. Binds Module
// -------------------------
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
}

// -------------------------
// 2. Provides Module
// -------------------------
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFakeStoreApiServices(): FakeStoreApiServices {
        // return actual implementation or Retrofit.create(...)
        // Replace this with your actual creation logic
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .build()
            .create(FakeStoreApiServices::class.java)
    }
}

