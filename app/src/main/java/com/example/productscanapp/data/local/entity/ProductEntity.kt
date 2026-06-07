package com.example.productscanapp.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val barcode: String,

    val name: String,

    val brand: String,

    val nutriScore: String?,

    val imageUrl: String?
)