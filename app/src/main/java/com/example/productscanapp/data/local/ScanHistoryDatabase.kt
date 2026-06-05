package com.example.productscanapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.entity.ScanHistoryEntity
import com.example.productscanapp.data.local.entity.FavoriteEntity
@Database(
    entities = [
        ScanHistoryEntity::class,
        FavoriteEntity::class
    ],
    version = 5
)
abstract class ScanHistoryDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao

    abstract fun favoriteDao(): FavoriteDao
}