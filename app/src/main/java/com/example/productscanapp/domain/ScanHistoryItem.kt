package com.example.productscanapp.domain

data class ScanHistoryItem(
    val product: Product,
    val scannedAt: Long,
    val favoriteAt: Long?
)
