package com.example.productscanapp.domain

import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    val history: Flow<List<ScanHistoryItem>>

    suspend fun saveScan(product: Product)

    suspend fun deleteScan(barcode: String)
}

