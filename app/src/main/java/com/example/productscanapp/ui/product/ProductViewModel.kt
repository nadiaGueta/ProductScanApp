package com.example.productscanapp.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.domain.ProductException
import com.example.productscanapp.domain.ProductRepository
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
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Idle)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    fun loadProduct(barcode: String) {
        if (barcode.isBlank()) {
            _uiState.value = ProductUiState.Error(ProductError.NotFound)
            return
        }

        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            val result = withContext(Dispatchers.IO) {
                repository.getProductByBarcode(barcode.trim())
            }

            result
                .onSuccess { product ->
                    _uiState.value = ProductUiState.Success(product)
                }
                .onFailure { throwable ->
                    val error = (throwable as? ProductException)?.error ?: ProductError.Unknown
                    _uiState.value = ProductUiState.Error(error)
                }
        }
    }
}

