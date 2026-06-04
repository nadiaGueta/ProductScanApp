package com.example.productscanapp.data.local

import com.example.productscanapp.data.local.entity.FavoriteEntity
import com.example.productscanapp.domain.Product

fun Product.toFavoriteEntity(
    favoriteAt: Long = System.currentTimeMillis()
): FavoriteEntity {
    return FavoriteEntity(
        barcode = barcode,
        name = name,
        brand = brand,
        nutriScore = nutriScore,
        imageUrl = imageUrl,
        favoriteAt = favoriteAt
    )
}

fun FavoriteEntity.toDomain(): Product {
    return Product(
        barcode = barcode,
        name = name,
        brand = brand,
        nutriScore = nutriScore,
        imageUrl = imageUrl
    )
}