package com.example.productscanapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.ui.common.BetterAlternativeBanner
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
    recommendationViewModel: RecommendationViewModel = hiltViewModel(),
) {
    val searchViewModel = productViewModel
    val searchUiState by searchViewModel.uiState.collectAsState()
    val scannerUiState by searchViewModel.uiState.collectAsState()
    val scannerIsFavorite by searchViewModel.isFavorite.collectAsState()

    var selectedTab by remember {
        mutableStateOf(AppTab.Recherche)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var scannerKey by remember {
        mutableIntStateOf(0)
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
                    onSearch = { barcode ->
                        searchViewModel.loadProduct(barcode)
                    }
                )
            }

            AppTab.Scanner -> {
                Box(modifier = Modifier.padding(innerPadding)) {
                    key(scannerKey) {
                        BarcodeScannerScreen(
                            onBarcodeDetected = { barcode ->
                                showDialog = true
                                searchViewModel.loadProduct(barcode)
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

            AppTab.Reglage -> {
                PlaceholderTab(
                    title = "Réglage",
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
                    searchViewModel.addToFavorites(product)
                },
                onRemoveFavorite = { product ->
                    searchViewModel.removeFromFavorites(product)
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
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    if (isFavorite) {
                        onRemoveFavorite(product)
                    } else {
                        onAddFavorite(product)
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.StarBorder
                    },
                    contentDescription = "Favori",
                    tint = if (isFavorite) {
                        Color(0xFFFFC107)
                    } else {
                        Color.Gray
                    }
                )
            }
        }

        Text(text = "Marque : ${product.brand}")

        Text(
            text = "NutriScore : ${product.nutriScore ?: "?"}",
            color = product.nutriScore.toNutriScoreColor()
        )

        betterAlternative?.let { alternative ->
            BetterAlternativeBanner(product = alternative)
        }

        ShareProductButton(product = product)
    }
}

private fun ProductError.toUserMessage(): String {
    return when (this) {
        ProductError.Network -> "Pas de connexion. Vérifie le réseau."
        ProductError.NotFound -> "Produit introuvable."
        ProductError.Unknown -> "Erreur inattendue."
    }
}

@Composable
private fun PlaceholderTab(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}