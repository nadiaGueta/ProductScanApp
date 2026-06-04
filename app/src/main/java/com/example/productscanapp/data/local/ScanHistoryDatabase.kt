package com.example.productscanapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.entity.ScanHistoryEntity

@Database(
    entities = [ScanHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ScanHistoryDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
}