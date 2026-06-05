package com.example.productscanapp.ui.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.productscanapp.ui.common.toNutriScoreColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    if (favorites.isEmpty()) {
        Text(
            text = "Aucun favori",
            modifier = modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(favorites) { product ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.size(90.dp)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Ajouté le : ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy HH:mm",
                                        Locale.FRANCE
                                    ).format(Date(product.favoriteAt))
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(product.brand)

                            Text(
                                text = product.brand,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "NutriScore ${product.nutriScore ?: "?"}",
                                color = product.nutriScore.toNutriScoreColor(),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.removeFromFavorites(product.barcode)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Retirer des favoris",
                                tint = Color(0xFFFFC107)
                            )
                        }
                    }
                }
            }
        }
    }
}