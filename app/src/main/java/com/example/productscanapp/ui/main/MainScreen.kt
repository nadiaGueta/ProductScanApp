package com.example.productscanapp.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.ui.common.BetterAlternativeBanner
import com.example.productscanapp.ui.common.FavoriteButton
import com.example.productscanapp.ui.common.ShareProductButton
import com.example.productscanapp.ui.common.toNutriScoreColor
import com.example.productscanapp.ui.favorite.FavoriteScreen
import com.example.productscanapp.ui.history.HistoryRoute
import com.example.productscanapp.ui.history.HistoryViewModel
import com.example.productscanapp.ui.product.ProductRoute
import com.example.productscanapp.ui.product.ProductUiState
import com.example.productscanapp.ui.product.ProductViewModel
import com.example.productscanapp.ui.scan.BarcodeScannerScreen
import com.example.productscanapp.ui.recommendation.RecommendationRoute
import com.example.productscanapp.ui.recommendation.RecommendationViewModel


@Composable
fun MainScreen(
    productViewModel: ProductViewModel,
    historyViewModel: HistoryViewModel,
    openScannerRequest: Int = 0,
    scannerViewModel: ProductViewModel = hiltViewModel(key = "scanner"),
    recommendationViewModel: RecommendationViewModel = hiltViewModel()
) {
    val searchViewModel = productViewModel
    val searchUiState by searchViewModel.uiState.collectAsState()
    val scannerUiState by scannerViewModel.uiState.collectAsState()
    val scannerIsFavorite by scannerViewModel.isFavorite.collectAsState()
    val searchIsFavorite by searchViewModel.isFavorite.collectAsState()
    val categoryState by searchViewModel.categoryState.collectAsState()
    var selectedTab by remember {
        mutableStateOf(AppTab.Recherche)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var scannerKey by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(openScannerRequest) {
        if (openScannerRequest > 0) {
            selectedTab = AppTab.Scanner
        }
    }

    Scaffold(
        bottomBar = {
            NavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.Recherche -> {
                ProductRoute(
                    modifier = Modifier.padding(innerPadding),
                    uiState = searchUiState,
                    categoryState = categoryState,
                    onSearch = { barcode ->
                        searchViewModel.loadProduct(barcode)
                    },
                    onCategorySearch = { category ->
                        searchViewModel.searchCategory(category)
                    },
                    onLoadNextPage = {
                        searchViewModel.loadNextCategoryPage()
                    },
                    onCategoryProductClick = { product ->
                        searchViewModel.showProduct(product)
                    },
                    isFavorite = searchIsFavorite,
                    onAddFavorite = { product ->
                        searchViewModel.addToFavorites(product)
                    },
                    onRemoveFavorite = { product ->
                        searchViewModel.removeFromFavorites(product)
                    }
                )
            }

            AppTab.Scanner -> {
                Box(modifier = Modifier.padding(innerPadding)) {
                    key(scannerKey) {
                        BarcodeScannerScreen(
                            onBarcodeDetected = { barcode ->
                                showDialog = true
                                scannerViewModel.loadProduct(barcode)
                            }
                        )
                    }
                }
            }

            AppTab.Rec -> {
                RecommendationRoute(
                    viewModel = recommendationViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppTab.Historique -> {
                HistoryRoute(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = historyViewModel,
                    onProductClick = { product ->
                        searchViewModel.showProduct(product)
                        selectedTab = AppTab.Recherche
                    }
                )
            }

            AppTab.Favoris -> {
                FavoriteScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (showDialog) {
            ProductDialog(
                uiState = scannerUiState,
                isFavorite = scannerIsFavorite,
                onDismiss = {
                    showDialog = false
                    scannerKey++
                },
                onAddFavorite = { product ->
                    scannerViewModel.addToFavorites(product)
                },
                onRemoveFavorite = { product ->
                    scannerViewModel.removeFromFavorites(product)
                }
            )
        }
    }
}

@Composable
private fun ProductDialog(
    uiState: ProductUiState,
    onDismiss: () -> Unit,
    isFavorite: Boolean,
    onAddFavorite: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit
) {
    when (uiState) {
        ProductUiState.Idle -> Unit

        ProductUiState.Loading -> {
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text("Recherche du produit")
                },
                text = {
                    Box(
                        modifier = Modifier.height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = {}
            )
        }

        is ProductUiState.Success -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text("Fiche produit")
                },
                text = {
                    ProductDialogContent(
                        product = uiState.product,
                        betterAlternative = uiState.betterAlternative,
                        isFavorite = isFavorite,
                        onAddFavorite = onAddFavorite,
                        onRemoveFavorite = onRemoveFavorite
                    )
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Fermer")
                    }
                }
            )
        }

        is ProductUiState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text("Erreur")
                },
                text = {
                    Text(uiState.error.toUserMessage())
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProductDialogContent(
    product: Product,
    betterAlternative: Product?,
    isFavorite: Boolean,
    onAddFavorite: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center,
        ) {
            if (!product.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(text = "📦", fontSize = 36.sp)
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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
            Text(
                text = product.brand,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            NutriScoreCircle(product.nutriScore)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = nutriScoreLabel(product.nutriScore),
                style = MaterialTheme.typography.bodyMedium,
                color = product.nutriScore.toNutriScoreColor(),
                fontWeight = FontWeight.Medium,
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

        betterAlternative?.let { alternative ->
            BetterAlternativeBanner(product = alternative)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(10.dp))
        }

        ShareProductButton(product = product)
    }
}

@Composable
private fun NutriScoreCircle(score: String?) {
    val letter = score?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(score.toNutriScoreColor()),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
        )
    }
}

private fun nutriScoreLabel(score: String?): String {
    return when (score?.uppercase()) {
        "A" -> "NutriScore A - Excellent"
        "B" -> "NutriScore B - Bon"
        "C" -> "NutriScore C - Moyen"
        "D" -> "NutriScore D - Mediocre"
        "E" -> "NutriScore E - Mauvais"
        else -> "NutriScore inconnu"
    }
}

private fun ProductError.toUserMessage(): String {
    return when (this) {
        ProductError.Network -> "Pas de connexion. Vérifie le réseau."
        ProductError.NotFound -> "Produit introuvable."
        ProductError.Unknown -> "Erreur inattendue."
    }
}