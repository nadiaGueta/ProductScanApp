package com.example.productscanapp.ui.common

import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.productscanapp.domain.Product

@Composable
fun ShareProductButton(
    product: Product
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val link = "myapp://product/${product.barcode}"

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, link)
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    "Partager le produit"
                )
            )
        }
    ) {
        Text("Partager")
    }
}