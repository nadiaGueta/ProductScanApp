package com.example.productscanapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.productscanapp.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Query("SELECT * FROM scan_history ORDER BY scannedAt DESC")
    fun observeAll(): Flow<List<ScanHistoryEntity>>

    @Upsert
    suspend fun upsert(item: ScanHistoryEntity)

    @Query("DELETE FROM scan_history WHERE barcode = :barcode")
    suspend fun deleteByBarcode(barcode: String)


    @Query("SELECT * FROM scan_history WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): ScanHistoryEntity?

    @Query("SELECT * FROM scan_history ORDER BY scannedAt DESC LIMIT 1")
    suspend fun getLatest(): ScanHistoryEntity?










}


