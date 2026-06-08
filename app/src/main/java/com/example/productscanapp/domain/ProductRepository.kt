package com.example.productscanapp.domain

interface ProductRepository {
    suspend fun getProductByBarcode(barcode: String): Result<Product>

    suspend fun addToFavorites(product: Product)

    suspend fun isFavorite(barcode: String): Boolean

    suspend fun removeFromFavorites(barcode: String)

    suspend fun getProductFromLocal(barcode: String): Product?

    suspend fun getBetterAlternative(product: Product): Product?
}