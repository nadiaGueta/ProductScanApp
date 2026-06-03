package com.example.productscanapp.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError

@Composable
fun ProductRoute(
    modifier: Modifier = Modifier,
    uiState: ProductUiState,
    onSearch: (String) -> Unit,
) {
    ProductScreen(
        uiState = uiState,
        onSearch = onSearch,
        modifier = modifier,
    )
}

@Composable
fun ProductScreen(
    uiState: ProductUiState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var barcode by remember { mutableStateOf("3017624010701") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Scan produit - US1",
            style = MaterialTheme.typography.headlineSmall,
        )

        OutlinedTextField(
            value = barcode,
            onValueChange = { barcode = it },
            label = { Text("Code-barres") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = { onSearch(barcode) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Rechercher")
        }

        when (uiState) {
            ProductUiState.Idle -> {
                Text("Entre un code-barres puis appuie sur Rechercher.")
            }

            ProductUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Chargement...")
                }
            }

            is ProductUiState.Success -> {
                ProductContent(product = uiState.product)
            }

            is ProductUiState.Error -> {
                Text(
                    text = uiState.error.toUserMessage(),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun ProductContent(product: Product) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )

            Text(text = product.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Marque: ${product.brand}")

            val nutri = product.nutriScore ?: "?"
            Text(
                text = "NutriScore: $nutri",
                color = product.nutriScore.toNutriScoreColor(),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

private fun ProductError.toUserMessage(): String {
    return when (this) {
        ProductError.Network -> "Pas de connexion. Vérifier le réseau et réessayer."
        ProductError.NotFound -> "Produit introuvable pour ce code-barre."
        ProductError.Unknown -> "Une erreur inattendue est survenue."
    }
}

private fun String?.toNutriScoreColor(): Color {
    return when (this?.uppercase()) {
        "A" -> Color(0xFF2E7D32)
        "B" -> Color(0xFF7CB342)
        "C" -> Color(0xFFFBC02D)
        "D" -> Color(0xFFFB8C00)
        "E" -> Color(0xFFC62828)
        else -> Color(0xFF37474F)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductScreenPreviewSuccess() {
    ProductScreen(
        uiState = ProductUiState.Success(
            product = Product(
                barcode = "3",
                name = "Nutella",
                brand = "Ferrero",
                nutriScore = "E",
                imageUrl = null,
            ),
        ),
        onSearch = {},
    )
}



