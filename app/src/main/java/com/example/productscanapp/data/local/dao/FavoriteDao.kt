package com.example.productscanapp.data.local.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.productscanapp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY favoriteAt DESC")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE barcode = :barcode")
    suspend fun deleteFavorite(barcode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE barcode = :barcode)")
    suspend fun isFavorite(barcode: String): Boolean
}