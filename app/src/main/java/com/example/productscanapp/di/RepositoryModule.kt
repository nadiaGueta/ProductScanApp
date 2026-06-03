package com.example.productscanapp.di

import com.example.productscanapp.data.DefaultProductRepository
import com.example.productscanapp.domain.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        implementation: DefaultProductRepository,
    ): ProductRepository
}

