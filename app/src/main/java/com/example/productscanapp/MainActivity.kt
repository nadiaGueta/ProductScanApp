package com.example.productscanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.productscanapp.ui.scan.BarcodeScannerScreen
import com.example.productscanapp.ui.product.ProductUiState
import com.example.productscanapp.ui.product.ProductRoute
import com.example.productscanapp.ui.product.ProductViewModel
import com.example.productscanapp.ui.theme.ProductScanAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            ProductScanAppTheme {
                BarcodeScannerScreen(
                    onBarcodeDetected = { barcode ->
                        println("CODE DETECTE = $barcode")
                    }
                MainAppScaffold(
                    uiState = uiState,
                    onSearch = viewModel::loadProduct,
                )
            }
        }
    }
}

@Composable
private fun MainAppScaffold(
    uiState: ProductUiState,
    onSearch: (String) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(AppTab.Recherche) }

    Scaffold(
        bottomBar = {
            NavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.Recherche -> ProductRoute(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onSearch = onSearch,
            )

            AppTab.Historique -> PlaceholderTab(
                title = "Historique",
                modifier = Modifier.padding(innerPadding),
            )

            AppTab.Favoris -> PlaceholderTab(
                title = "Favoris",
                modifier = Modifier.padding(innerPadding),
            )

            AppTab.Reglage -> PlaceholderTab(
                title = "Réglage",
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun PlaceholderTab(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title)
    }
}

@Preview(showBackground = true)
@Composable
fun ProductPreview() {
    ProductScanAppTheme {
        MainAppScaffold(
            uiState = ProductUiState.Idle,
            onSearch = {},
        )
    }
}