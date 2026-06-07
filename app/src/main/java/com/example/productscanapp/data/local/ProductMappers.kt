package com.example.productscanapp.data.local



import com.example.productscanapp.data.local.entity.ProductEntity
import com.example.productscanapp.domain.Product

fun ProductEntity.toDomain(): Product {
    return Product(
        barcode = barcode,
        name = name,
        brand = brand,
        nutriScore = nutriScore,
        imageUrl = imageUrl
    )
}

fun Product.toProductEntity(): ProductEntity {
    return ProductEntity(
        barcode = barcode,
        name = name,
        brand = brand,
        nutriScore = nutriScore,
        imageUrl = imageUrl
    )
}