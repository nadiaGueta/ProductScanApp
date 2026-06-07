package com.example.productscanapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.dao.ProductDao
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.entity.ScanHistoryEntity
import com.example.productscanapp.data.local.entity.FavoriteEntity
import com.example.productscanapp.data.local.entity.ProductEntity
@Database(
    entities = [
        ScanHistoryEntity::class,
        FavoriteEntity::class ,
        ProductEntity::class
    ],
    version = 6
)
abstract class ScanHistoryDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun productDao(): ProductDao
}