package com.example.productscanapp.di

import android.content.Context
import androidx.room.Room
import com.example.productscanapp.data.local.ScanHistoryDatabase
import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.dao.ProductDao
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "product_scan.db"

    @Provides
    @Singleton
    fun provideScanHistoryDatabase(
        @ApplicationContext context: Context,
    ): ScanHistoryDatabase {
        return Room.databaseBuilder(
            context,
            ScanHistoryDatabase::class.java,
            DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideScanHistoryDao(database: ScanHistoryDatabase): ScanHistoryDao {
        return database.scanHistoryDao()
    }


    @Provides
    @Singleton
    fun provideFavoriteDao(
        database: ScanHistoryDatabase
    ): FavoriteDao {
        return database.favoriteDao()
    }



    @Provides
    @Singleton
    fun provideProductDao(
        database: ScanHistoryDatabase
    ): ProductDao {
        return database.productDao()
    }
}

