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
import javax.inject.Singleton

interface Module {
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryBindingModule{
        @Binds
        @Singleton
        abstract fun bindingProductRepository(
            productRepositoryImpl : ProductRepositoryImpl
        ): ProductRepository
        @Binds
        @Singleton
        abstract fun bindCategoryRepository(
            categoryRepositoryImpl: CategoryRepositoryImpl
        ):CategoryRepository



    }
















  @Provides
  @Singleton
  fun provideFakeStoreApiServices() : FakeStoreApiServices{
      return provideFakeStoreApiServices()
  }

}