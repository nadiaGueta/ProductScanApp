package com.example.productscanapp.ui.common

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.productscanapp.domain.Product

@Composable
fun ShareProductButton(product: Product) {
    val context = LocalContext.current

    Button(
        onClick = {
            val link = "myapp://product/${product.barcode}"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, link)
            }
            context.startActivity(
                Intent.createChooser(intent, "Partager le produit")
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6C63FF),
            contentColor = Color.White,
        ),
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Partager ce produit")
    }
}