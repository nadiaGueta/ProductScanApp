package com.example.productscanapp.data

import android.content.Context
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.toDomainItem
import com.example.productscanapp.data.local.toEntity
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ScanHistoryItem
import com.example.productscanapp.domain.ScanHistoryRepository
import com.example.productscanapp.ui.widget.LastScanWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultScanHistoryRepository @Inject constructor(
    private val dao: ScanHistoryDao,
    @ApplicationContext private val context: Context
) : ScanHistoryRepository {

    override val history: Flow<List<ScanHistoryItem>> =
        dao.observeAll()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomainItem()
                }
            }

    override suspend fun saveScan(product: Product) {
        dao.upsert(
            product.toEntity(
                scannedAt = System.currentTimeMillis()
            )
        )

        LastScanWidget.updateAll(context)
    }

    override suspend fun deleteScan(barcode: String) {
        dao.deleteByBarcode(barcode)
        LastScanWidget.updateAll(context)
    }
}