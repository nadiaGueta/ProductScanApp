package com.example.productscanapp.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.domain.ProductException
import com.example.productscanapp.domain.ProductRepository
import com.example.productscanapp.domain.ScanHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val scanHistoryRepository: ScanHistoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Idle)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private var lastLoadedBarcode: String? = null
    private var isLoadingInProgress = false

    fun loadProduct(
        barcode: String,
        saveInHistory: Boolean = true
    ) {
        if (isLoadingInProgress) {
            return
        }

        if (barcode.isBlank()) {
            _uiState.value = ProductUiState.Error(ProductError.NotFound)
            return
        }

        val trimmedBarcode = barcode.trim()

        if (lastLoadedBarcode == trimmedBarcode && _uiState.value is ProductUiState.Success) {
            return
        }

        isLoadingInProgress = true
        lastLoadedBarcode = trimmedBarcode

        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading

            val result = withContext(Dispatchers.IO) {
                repository.getProductByBarcode(trimmedBarcode)
            }

            result
                .onSuccess { product ->
                    if (saveInHistory) {
                        withContext(Dispatchers.IO) {
                            scanHistoryRepository.saveScan(product)
                        }
                    }

                    _uiState.value = ProductUiState.Success(product)

                    if (product.nutriScore?.uppercase() in setOf("D", "E")) {
                        val alternative = withContext(Dispatchers.IO) {
                            repository.getBetterAlternative(product)
                        }

                        _uiState.value = ProductUiState.Success(
                            product = product,
                            betterAlternative = alternative
                        )
                    }
                    _isFavorite.value = repository.isFavorite(product.barcode)
                }
                .onFailure { throwable ->
                    val error =
                        (throwable as? ProductException)?.error
                            ?: ProductError.Unknown

                    _uiState.value = ProductUiState.Error(error)
                }
                .also {
                    isLoadingInProgress = false
                }
        }
    }

    fun showProduct(product: Product) {
        _uiState.value = ProductUiState.Success(product)
    }


    fun addToFavorites(product: Product) {
        viewModelScope.launch {
            repository.addToFavorites(product)
            _isFavorite.value = true
        }
    }
    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            repository.removeFromFavorites(product.barcode)
            _isFavorite.value = false
        }
    }
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun showInvalidLinkError() {
        _uiState.value = ProductUiState.Error(ProductError.Unknown)
    }
    }



