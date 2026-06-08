package com.example.productscanapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.productscanapp.ui.history.HistoryViewModel
import com.example.productscanapp.ui.main.MainScreen
import com.example.productscanapp.ui.product.ProductViewModel
import com.example.productscanapp.ui.theme.ProductScanAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val productViewModel: ProductViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    private var openScannerRequest by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        handleIntent(intent)

        setContent {
            ProductScanAppTheme {
                MainScreen(
                    productViewModel = productViewModel,
                    historyViewModel = historyViewModel,
                    openScannerRequest = openScannerRequest
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == ACTION_OPEN_SCANNER) {
            openScannerRequest++
            return
        }

        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri: Uri = intent?.data ?: return

        if (uri.scheme == "myapp" && uri.host == "product") {
            val barcode = uri.lastPathSegment

            if (!barcode.isNullOrBlank()) {
                productViewModel.loadProduct(
                    barcode = barcode,
                    saveInHistory = false
                )
            } else {
                productViewModel.showInvalidLinkError()
            }
        } else {
            productViewModel.showInvalidLinkError()
        }
    }

    companion object {
        const val ACTION_OPEN_SCANNER =
            "com.example.productscanapp.action.OPEN_SCANNER"
    }
}