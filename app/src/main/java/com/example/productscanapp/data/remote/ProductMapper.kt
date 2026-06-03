package com.example.productscanapp.data.remote

import com.example.productscanapp.domain.Product

fun ProductDto.toDomain(barcode: String): Product {
    return Product(
        barcode = barcode,
        name = productName ?: "Nom inconnu",
        brand = brands ?: "Marque inconnue",
        nutriScore = nutriScore?.uppercase(),
        imageUrl = imageUrl
    )
}