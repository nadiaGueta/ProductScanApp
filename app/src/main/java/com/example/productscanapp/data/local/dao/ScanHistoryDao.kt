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


    @Query("""
UPDATE scan_history
SET isFavorite = :isFavorite,
    favoriteAt = :favoriteAt
WHERE barcode = :barcode
""")
    suspend fun updateFavorite(
        barcode: String,
        isFavorite: Boolean,
        favoriteAt: Long?
    )


    @Query("""
SELECT *
FROM scan_history
WHERE isFavorite = 1
ORDER BY favoriteAt DESC
""")
    fun observeFavorites(): Flow<List<ScanHistoryEntity>>



    @Query("SELECT isFavorite FROM scan_history WHERE barcode = :barcode LIMIT 1")
    suspend fun isFavorite(barcode: String): Boolean?

    @Query("SELECT favoriteAt FROM scan_history WHERE barcode = :barcode LIMIT 1")
    suspend fun getFavoriteAt(barcode: String): Long?
}



