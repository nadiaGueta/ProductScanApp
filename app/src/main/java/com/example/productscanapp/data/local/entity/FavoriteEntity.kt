package com.example.productscanapp.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val brand: String,
    val nutriScore: String?,
    val imageUrl: String?,
    val favoriteAt: Long
)