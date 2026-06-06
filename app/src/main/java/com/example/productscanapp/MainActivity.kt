package com.example.productscanapp

import android.content.Intent
import android.net.Uri
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
        handleDeepLink(intent)
        setContent {
            ProductScanAppTheme {
                MainScreen(
                    productViewModel = productViewModel,
                    historyViewModel = historyViewModel,
                )
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri: Uri = intent?.data ?: return

        if (uri.scheme == "myapp" && uri.host == "product") {
            val barcode = uri.lastPathSegment

            if (!barcode.isNullOrBlank()) {
                productViewModel.loadProduct(barcode)
            } else {
                productViewModel.showInvalidLinkError()
            }
        } else {
            productViewModel.showInvalidLinkError()
        }
    }
}