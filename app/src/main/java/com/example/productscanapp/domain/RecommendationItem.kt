package com.example.productscanapp.domain

data class RecommendationItem(
    val scannedProduct: Product,
    val alternative: Product
)