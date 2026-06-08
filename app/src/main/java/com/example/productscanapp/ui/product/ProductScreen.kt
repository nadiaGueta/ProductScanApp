package com.example.productscanapp.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.ui.common.ShareProductButton

private fun String?.toNutriScoreColor(): Color = when (this?.uppercase()) {
    "A" -> Color(0xFF3AB547)
    "B" -> Color(0xFF85BB2F)
    "C" -> Color(0xFFF5A623)
    "D" -> Color(0xFFE07B39)
    "E" -> Color(0xFFE63946)
    else -> Color(0xFFBDBDBD)
}

private fun String?.toNutriScoreLabel(): String = when (this?.uppercase()) {
    "A" -> "Excellent"
    "B" -> "Bon"
    "C" -> "Moyen"
    "D" -> "Médiocre"
    "E" -> "Mauvais"
    else -> "Non évalué"
}

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
    var barcode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                placeholder = { Text("Code-barres produit", color = Color(0xFFBDBDBD)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        tint = Color(0xFF6C63FF),
                        modifier = Modifier.size(22.dp),
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { if (barcode.isNotBlank()) onSearch(barcode) },
                ),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                modifier = Modifier.weight(1f),
            )

            Button(
                onClick = { if (barcode.isNotBlank()) onSearch(barcode) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                modifier = Modifier.size(56.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        when (uiState) {
            ProductUiState.Idle -> IdleState()
            ProductUiState.Loading -> LoadingState()
            is ProductUiState.Success -> ProductContent(product = uiState.product)
            is ProductUiState.Error -> ErrorState(error = uiState.error)
        }
    }
}

@Composable
private fun IdleState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF6C63FF).copy(alpha = 0.3f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Rechercher un produit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Entrez un code-barres pour\nobtenir les infos nutritionnelles",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFF6C63FF),
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recherche en cours…",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF9E9E9E),
            )
        }
    }
}

@Composable
private fun ErrorState(error: ProductError) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "😕", fontSize = 52.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error.toUserMessage(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE63946),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ProductContent(product: Product) {
    val score = product.nutriScore

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {

            // Image produit
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFF8F8F8)),
                contentAlignment = Alignment.Center,
            ) {
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(16.dp),
                    )
                } else {
                    Text(text = "📦", fontSize = 64.sp)
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {

                // Nom + marque
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                )
                if (product.brand.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9E9E9E),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(16.dp))

                // NutriScore
                Text(
                    text = "Nutri-Score",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(score.toNutriScoreColor()),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = score?.uppercase() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = score.toNutriScoreLabel(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = score.toNutriScoreColor(),
                        )
                        Text(
                            text = "Qualité nutritionnelle",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBDBDBD),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ShareProductButton(product = product)
            }
        }
    }
}

private fun ProductError.toUserMessage(): String = when (this) {
    ProductError.Network -> "Pas de connexion réseau.\nVérifiez votre connexion et réessayez."
    ProductError.NotFound -> "Produit introuvable\npour ce code-barres."
    ProductError.Unknown -> "Une erreur inattendue est survenue."
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8)
@Composable
private fun ProductScreenPreviewSuccess() {
    ProductScreen(
        uiState = ProductUiState.Success(
            product = Product(
                barcode = "3017624010701",
                name = "Nutella",
                brand = "Ferrero",
                nutriScore = "E",
                imageUrl = null,
            ),
        ),
        onSearch = {},
    )
}


