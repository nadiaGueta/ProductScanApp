package com.example.productscanapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.ui.product.ProductRoute
import com.example.productscanapp.ui.product.ProductUiState
import com.example.productscanapp.ui.product.ProductViewModel
import com.example.productscanapp.ui.scan.BarcodeScannerScreen

@Composable
fun MainScreen(
    searchViewModel: ProductViewModel = hiltViewModel(key = "search"),
    scannerViewModel: ProductViewModel = hiltViewModel(key = "scanner")
) {
    val searchUiState by searchViewModel.uiState.collectAsState()
    val scannerUiState by scannerViewModel.uiState.collectAsState()
    var selectedTab by remember {
        mutableStateOf(AppTab.Recherche)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }
    var scannerKey by remember { mutableIntStateOf(0) }
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
                                scannerViewModel.loadProduct(barcode)                            }
                        )
                    }
                }
            }


            AppTab.Historique -> {
                PlaceholderTab("Historique", Modifier.padding(innerPadding))
            }

            AppTab.Favoris -> {
                PlaceholderTab("Favoris", Modifier.padding(innerPadding))
            }

            AppTab.Reglage -> {
                PlaceholderTab("Réglage", Modifier.padding(innerPadding))
            }
        }

        if (showDialog) {
            ProductDialog(
                uiState = scannerUiState,
                onDismiss = {
                   showDialog = false
                    scannerKey++
                }
            )
        }
    }
}


@Composable
private fun ProductDialog(
    uiState: ProductUiState,
    onDismiss: () -> Unit
) {
    when (uiState) {

        ProductUiState.Idle -> Unit

        ProductUiState.Loading -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Recherche du produit") },
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
                title = { Text("Produit trouvé") },
                text = {
                    ProductDialogContent(product = uiState.product)
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
                title = { Text("Erreur") },
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
    product: Product
) {
    Column {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier.height(160.dp)
        )

        Text("Nom : ${product.name}")
        Text("Marque : ${product.brand}")
        Text("NutriScore : ${product.nutriScore ?: "?"}")
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