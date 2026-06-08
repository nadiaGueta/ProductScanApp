package com.example.productscanapp.ui.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productscanapp.domain.ProductRepository
import com.example.productscanapp.domain.RecommendationItem
import com.example.productscanapp.domain.ScanHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    scanHistoryRepository: ScanHistoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    val recommendations: StateFlow<List<RecommendationItem>> =
        scanHistoryRepository.history
            .map { history ->
                history.mapNotNull { historyItem ->
                    val product = historyItem.product
                    val score = product.nutriScore?.uppercase()

                    if (score !in setOf("D", "E")) {
                        return@mapNotNull null
                    }

                    val alternative =
                        productRepository.getBetterAlternative(product)
                            ?: return@mapNotNull null

                    RecommendationItem(
                        scannedProduct = product,
                        alternative = alternative
                    )
                }
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}