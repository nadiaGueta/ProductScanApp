package com.example.productscanapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.productscanapp.domain.Product

@Composable
fun BetterAlternativeBanner(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFE8F5E9),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = "Meilleure alternative",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1B5E20)
        )

        Text(text = product.name)

        Text(
            text = "NutriScore : ${product.nutriScore}",
            color = product.nutriScore.toNutriScoreColor()
        )
    }
}