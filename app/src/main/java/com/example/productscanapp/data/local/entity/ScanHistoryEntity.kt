package com.example.productscanapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scan_history",
    indices = [Index(value = ["scannedAt"])],
)
data class ScanHistoryEntity(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val brand: String,
    val nutriScore: String?,
    val imageUrl: String?,
    val scannedAt: Long,
)

