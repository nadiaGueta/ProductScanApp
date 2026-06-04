package com.example.productscanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.productscanapp.ui.history.HistoryViewModel
import com.example.productscanapp.ui.main.MainScreen
import com.example.productscanapp.ui.product.ProductViewModel
import com.example.productscanapp.ui.theme.ProductScanAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val productViewModel: ProductViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProductScanAppTheme {
                MainScreen(
                    productViewModel = productViewModel,
                    historyViewModel = historyViewModel,
                )
            }
        }
    }
}