package com.example.productscanapp.domain

data class Product(
    val barcode: String,
    val name: String,
    val brand: String,
    val nutriScore: String?,
    val imageUrl: String?
)