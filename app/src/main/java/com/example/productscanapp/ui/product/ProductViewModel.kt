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
    private val scanHistoryRepository: ScanHistoryRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ProductUiState>(ProductUiState.Idle)

    val uiState: StateFlow<ProductUiState> =
        _uiState.asStateFlow()

    private val _categoryState =
        MutableStateFlow(CategorySearchUiState())

    val categoryState: StateFlow<CategorySearchUiState> =
        _categoryState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)

    val isFavorite: StateFlow<Boolean> =
        _isFavorite.asStateFlow()

    private var lastLoadedBarcode: String? = null
    private var isProductLoading = false

    private var currentCategory = ""
    private var currentPage = 1
    private val pageSize = 7

    fun loadProduct(
        barcode: String,
        saveInHistory: Boolean = true
    ) {
        if (isProductLoading) return

        val trimmedBarcode = barcode.trim()

        if (trimmedBarcode.isBlank()) {
            _uiState.value =
                ProductUiState.Error(ProductError.NotFound)
            return
        }

        if (
            lastLoadedBarcode == trimmedBarcode &&
            _uiState.value is ProductUiState.Success
        ) {
            return
        }

        isProductLoading = true
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

                    val alternative =
                        if (
                            product.nutriScore?.uppercase()
                            in setOf("D", "E")
                        ) {
                            withContext(Dispatchers.IO) {
                                repository.getBetterAlternative(product)
                            }
                        } else {
                            null
                        }

                    _uiState.value = ProductUiState.Success(
                        product = product,
                        betterAlternative = alternative
                    )

                    _isFavorite.value =
                        repository.isFavorite(product.barcode)
                }
                .onFailure { throwable ->
                    _uiState.value = ProductUiState.Error(
                        throwable.toProductError()
                    )
                }

            isProductLoading = false
        }
    }

    fun searchCategory(category: String) {
        val normalizedCategory = category
            .trim()
            .lowercase()
            .replace(" ", "-")

        if (normalizedCategory.isBlank()) return

        currentCategory = normalizedCategory
        currentPage = 1

        _categoryState.value = CategorySearchUiState(
            isInitialLoading = true
        )

        loadCategoryPage(reset = true)
    }

    fun loadNextCategoryPage() {
        val state = _categoryState.value

        if (
            currentCategory.isBlank() ||
            state.isInitialLoading ||
            state.isLoadingNextPage ||
            !state.canLoadMore
        ) {
            return
        }

        currentPage++
        loadCategoryPage(reset = false)
    }

    private fun loadCategoryPage(reset: Boolean) {
        viewModelScope.launch {
            if (!reset) {
                _categoryState.value =
                    _categoryState.value.copy(
                        isLoadingNextPage = true,
                        error = null
                    )
            }

            val result = withContext(Dispatchers.IO) {
                repository.searchProductsByCategory(
                    category = currentCategory,
                    page = currentPage,
                    pageSize = pageSize
                )
            }

            result
                .onSuccess { newProducts ->
                    val previousProducts =
                        if (reset) {
                            emptyList()
                        } else {
                            _categoryState.value.products
                        }

                    val sortedProducts =
                        (previousProducts + newProducts)
                            .distinctBy { it.barcode }
                            .sortedBy { product ->
                                product.nutriScore.sortOrder()
                            }

                    _categoryState.value =
                        CategorySearchUiState(
                            products = sortedProducts,
                            isInitialLoading = false,
                            isLoadingNextPage = false,
                            canLoadMore =
                                newProducts.size == pageSize,
                            error = null
                        )
                }
                .onFailure { throwable ->
                    if (!reset) {
                        currentPage--
                    }

                    _categoryState.value =
                        _categoryState.value.copy(
                            isInitialLoading = false,
                            isLoadingNextPage = false,
                            error = throwable.toProductError()
                        )
                }
        }
    }

    fun showProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Success(product)

            _isFavorite.value =
                repository.isFavorite(product.barcode)
        }
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

    fun showInvalidLinkError() {
        _uiState.value =
            ProductUiState.Error(ProductError.Unknown)
    }

    private fun Throwable.toProductError(): ProductError {
        return (this as? ProductException)?.error
            ?: ProductError.Unknown
    }

    private fun String?.sortOrder(): Int {
        return when (this?.uppercase()) {
            "A" -> 0
            "B" -> 1
            "C" -> 2
            "D" -> 3
            "E" -> 4
            else -> 5
        }
    }
}