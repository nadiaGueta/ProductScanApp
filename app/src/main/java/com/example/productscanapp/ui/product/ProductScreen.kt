package com.example.productscanapp.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.compose.AsyncImage
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.ui.common.FavoriteButton
import com.example.productscanapp.ui.common.ShareProductButton

private enum class ProductSearchMode {
    Barcode,
    Category
}

private fun String?.toNutriScoreColor(): Color {
    return when (this?.uppercase()) {
        "A" -> Color(0xFF3AB547)
        "B" -> Color(0xFF85BB2F)
        "C" -> Color(0xFFF5A623)
        "D" -> Color(0xFFE07B39)
        "E" -> Color(0xFFE63946)
        else -> Color(0xFFBDBDBD)
    }
}

private fun String?.toNutriScoreLabel(): String {
    return when (this?.uppercase()) {
        "A" -> "Excellent"
        "B" -> "Bon"
        "C" -> "Moyen"
        "D" -> "Médiocre"
        "E" -> "Mauvais"
        else -> "Non évalué"
    }
}

@Composable
fun ProductRoute(
    modifier: Modifier = Modifier,
    uiState: ProductUiState,
    categoryState: CategorySearchUiState,
    onSearch: (String) -> Unit,
    onCategorySearch: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    onCategoryProductClick: (Product) -> Unit,
    isFavorite: Boolean,
    onAddFavorite: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit
) {
    ProductScreen(
        modifier = modifier,
        uiState = uiState,
        categoryState = categoryState,
        onSearch = onSearch,
        onCategorySearch = onCategorySearch,
        onLoadNextPage = onLoadNextPage,
        onCategoryProductClick = onCategoryProductClick,
        isFavorite = isFavorite,
        onAddFavorite = onAddFavorite,
        onRemoveFavorite = onRemoveFavorite
    )
}

@Composable
fun ProductScreen(
    uiState: ProductUiState,
    categoryState: CategorySearchUiState,
    onSearch: (String) -> Unit,
    onCategorySearch: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    onCategoryProductClick: (Product) -> Unit,
    isFavorite: Boolean,
    onAddFavorite: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember {
        mutableStateOf(ProductSearchMode.Barcode)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        SearchModeSelector(
            selectedMode = mode,
            onModeSelected = { mode = it }
        )

        Spacer(modifier = Modifier.height(14.dp))

        when (mode) {
            ProductSearchMode.Barcode -> {
                BarcodeSearchContent(
                    uiState = uiState,
                    onSearch = onSearch,
                    isFavorite = isFavorite,
                    onAddFavorite = onAddFavorite,
                    onRemoveFavorite = onRemoveFavorite,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            ProductSearchMode.Category -> {
                CategorySearchContent(
                    state = categoryState,
                    onSearch = onCategorySearch,
                    onLoadNextPage = onLoadNextPage,
                    onProductClick = { product ->
                        mode = ProductSearchMode.Barcode
                        onCategoryProductClick(product)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SearchModeSelector(
    selectedMode: ProductSearchMode,
    onModeSelected: (ProductSearchMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchModeButton(
            text = "Code-barres",
            selected = selectedMode == ProductSearchMode.Barcode,
            onClick = {
                onModeSelected(ProductSearchMode.Barcode)
            },
            modifier = Modifier.weight(1f)
        )

        SearchModeButton(
            text = "Catégorie",
            selected = selectedMode == ProductSearchMode.Category,
            onClick = {
                onModeSelected(ProductSearchMode.Category)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SearchModeButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF)
            )
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = text,
                color = Color(0xFF6C63FF)
            )
        }
    }
}

@Composable
private fun BarcodeSearchContent(
    uiState: ProductUiState,
    onSearch: (String) -> Unit,
    isFavorite: Boolean,
    onAddFavorite: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var barcode by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                placeholder = {
                    Text(
                        text = "Code-barres produit",
                        color = Color(0xFFBDBDBD)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        tint = Color(0xFF6C63FF),
                        modifier = Modifier.size(22.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (barcode.isNotBlank()) {
                            onSearch(barcode)
                        }
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (barcode.isNotBlank()) {
                        onSearch(barcode)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                modifier = Modifier.size(56.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        when (uiState) {
            ProductUiState.Idle -> IdleState()
            ProductUiState.Loading -> LoadingState()

            is ProductUiState.Success -> {
                ProductContent(
                    product = uiState.product,
                    isFavorite = isFavorite,
                    onAddFavorite = onAddFavorite,
                    onRemoveFavorite = onRemoveFavorite
                )
            }

            is ProductUiState.Error -> {
                ErrorState(error = uiState.error)
            }
        }
    }
}

@Composable
private fun CategorySearchContent(
    state: CategorySearchUiState,
    onSearch: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var category by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                placeholder = {
                    Text(
                        text = "Ex. chocolate-spreads",
                        color = Color(0xFFBDBDBD)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = Color(0xFF6C63FF)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (category.isNotBlank()) {
                            onSearch(category)
                        }
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (category.isNotBlank()) {
                        onSearch(category)
                    }
                },
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Rechercher la catégorie",
                    tint = Color.White
                )
            }
        }

        when {
            state.isInitialLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingState()
                }
            }

            state.error != null && state.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorState(error = state.error)
                }
            }

            state.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = Color(0xFF6C63FF).copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Rechercher une catégorie",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A2E)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Les produits seront triés par NutriScore",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        RecyclerView(context).apply {
                            layoutManager = LinearLayoutManager(context)

                            adapter = CategoryProductAdapter(
                                onProductClick = onProductClick
                            )

                            addOnScrollListener(
                                object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(
                                        recyclerView: RecyclerView,
                                        dx: Int,
                                        dy: Int
                                    ) {
                                        super.onScrolled(
                                            recyclerView,
                                            dx,
                                            dy
                                        )

                                        if (
                                            dy > 0 &&
                                            !recyclerView.canScrollVertically(1)
                                        ) {
                                            onLoadNextPage()
                                        }
                                    }
                                }
                            )
                        }
                    },
                    update = { recyclerView ->
                        val adapter =
                            recyclerView.adapter
                                    as CategoryProductAdapter

                        adapter.submit(
                            products = state.products,
                            loading = state.isLoadingNextPage
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun IdleState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF6C63FF).copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rechercher un produit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Entrez un code-barres pour\nobtenir les infos nutritionnelles",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF6C63FF),
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recherche en cours…",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
private fun ErrorState(error: ProductError) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Une erreur est survenue",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFE63946),
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error.toUserMessage(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProductContent(
    product: Product,
    isFavorite: Boolean,
    onAddFavorite: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit
) {
    val score = product.nutriScore

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFF8F8F8)),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(16.dp)
                    )
                } else {
                    Text(
                        text = "Aucune image",
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E),
                        modifier = Modifier.weight(1f)
                    )

                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = {
                            if (isFavorite) {
                                onRemoveFavorite(product)
                            } else {
                                onAddFavorite(product)
                            }
                        }
                    )
                }

                if (product.brand.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9E9E9E)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nutri-Score",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(score.toNutriScoreColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = score?.uppercase() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = score.toNutriScoreLabel(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = score.toNutriScoreColor()
                        )

                        Text(
                            text = "Qualité nutritionnelle",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBDBDBD)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                ShareProductButton(product = product)
            }
        }
    }
}

private fun ProductError.toUserMessage(): String = when (this) {
    ProductError.Network -> "Pas de connexion réseau.\nVérifiez votre connexion et réessayez."
    ProductError.NotFound -> "Produit introuvable\npour ce code-barres."
    ProductError.Unknown -> "Une erreur inattendue est survenue."
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8)
@Composable
private fun ProductScreenPreviewSuccess() {
    ProductScreen(
        uiState = ProductUiState.Success(
            product = Product(
                barcode = "3017624010701",
                name = "Nutella",
                brand = "Ferrero",
                nutriScore = "E",
                imageUrl = null
            )
        ),
        categoryState = CategorySearchUiState(),
        onSearch = {},
        onCategorySearch = {},
        onLoadNextPage = {},
        onCategoryProductClick = {},
        isFavorite = false,
        onAddFavorite = {},
        onRemoveFavorite = {}
    )
}