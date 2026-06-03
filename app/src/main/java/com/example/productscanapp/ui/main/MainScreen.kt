package com.example.productscanapp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.productscanapp.ui.product.ProductRoute
import com.example.productscanapp.ui.product.ProductViewModel
import com.example.productscanapp.ui.scan.BarcodeScannerScreen

@Composable
fun MainScreen(
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by productViewModel.uiState.collectAsState()

    var selectedTab by remember {
        mutableStateOf(AppTab.Recherche)
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
                    uiState = uiState,
                    onSearch = { barcode ->
                        productViewModel.loadProduct(barcode)
                    }
                )
            }

            AppTab.Scanner -> {
                BarcodeScannerScreen(
                    onBarcodeDetected = { barcode ->
                        productViewModel.loadProduct(barcode)
                        selectedTab = AppTab.Recherche
                    }
                )
            }

            AppTab.Historique -> {
                PlaceholderTab(
                    title = "Historique",
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppTab.Favoris -> {
                PlaceholderTab(
                    title = "Favoris",
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