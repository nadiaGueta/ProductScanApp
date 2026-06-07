package com.example.productscanapp.data.local.dao



import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.productscanapp.data.local.entity.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): ProductEntity?

    @Upsert
    suspend fun upsert(product: ProductEntity)
}