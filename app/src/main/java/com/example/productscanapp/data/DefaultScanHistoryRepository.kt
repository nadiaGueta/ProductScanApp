package com.example.productscanapp.data

import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.toDomain
import com.example.productscanapp.data.local.toDomainItem
import com.example.productscanapp.data.local.toEntity
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ScanHistoryItem
import com.example.productscanapp.domain.ScanHistoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultScanHistoryRepository @Inject constructor(
    private val dao: ScanHistoryDao,
) : ScanHistoryRepository {

    override val history: Flow<List<ScanHistoryItem>> = dao
        .observeAll()
        .map { entities -> entities.map { it.toDomainItem() } }

    override suspend fun saveScan(product: Product) {
        dao.upsert(product.toEntity(scannedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteScan(barcode: String) {
        dao.deleteByBarcode(barcode)
    }
}

