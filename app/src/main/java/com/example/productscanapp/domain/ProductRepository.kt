package com.example.productscanapp.domain

interface ProductRepository {
    suspend fun getProductByBarcode(barcode: String): Result<Product>
}