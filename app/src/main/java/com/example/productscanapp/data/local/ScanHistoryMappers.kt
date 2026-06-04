package com.example.productscanapp.data.local

import com.example.productscanapp.data.local.entity.ScanHistoryEntity
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ScanHistoryItem

fun ScanHistoryEntity.toDomain(): Product {
    return Product(
        barcode = barcode,
        name = name,
        brand = brand,
        nutriScore = nutriScore,
        imageUrl = imageUrl,
    )
}
fun ScanHistoryEntity.toDomainItem(): ScanHistoryItem {
    return ScanHistoryItem(
        product = toDomain(),
        scannedAt = scannedAt,
        favoriteAt = favoriteAt
    )
}


fun Product.toEntity(
    scannedAt: Long,
    isFavorite: Boolean = false,
    favoriteAt: Long? = null
): ScanHistoryEntity {
    return ScanHistoryEntity(
        barcode = barcode,
        name = name,
        brand = brand,
        nutriScore = nutriScore,
        imageUrl = imageUrl,
        scannedAt = scannedAt,
        isFavorite = isFavorite,
        favoriteAt = favoriteAt
    )
}
