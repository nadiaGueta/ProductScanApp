package com.example.productscanapp.ui.favorite

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.productscanapp.data.local.entity.FavoriteEntity
import java.text.DateFormat
import java.util.Date
import java.util.Locale

private val FavoriteItemShape = RoundedCornerShape(18.dp)

private fun nutriScoreColor(score: String?): Color = when (score?.uppercase()) {
    "A" -> Color(0xFF3AB547)
    "B" -> Color(0xFF85BB2F)
    "C" -> Color(0xFFF5A623)
    "D" -> Color(0xFFE07B39)
    "E" -> Color(0xFFE63946)
    else -> Color(0xFFBDBDBD)
}

private fun nutriScoreLabel(score: String?): String = when (score?.uppercase()) {
    "A" -> "Excellent"
    "B" -> "Bon"
    "C" -> "Moyen"
    "D" -> "Médiocre"
    "E" -> "Mauvais"
    else -> "?"
}

private fun formatDate(timeMillis: Long): String {
    val date = Date(timeMillis)
    val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)
    val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.FRANCE)
    return "Ajouté le : ${dateFormatter.format(date)} à ${timeFormatter.format(date)}"
}

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = "Favoris",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Retrouve tes produits préférés",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aucun favori",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Vos produits préférés apparaîtront ici",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(favorites, key = { it.barcode }) { favorite ->
                    FavoriteItem(
                        favorite = favorite,
                        onRemoveFavorite = { viewModel.removeFromFavorites(favorite.barcode) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteItem(
    favorite: FavoriteEntity,
    onRemoveFavorite: () -> Unit,
) {
    val score = favorite.nutriScore

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = FavoriteItemShape),
        shape = FavoriteItemShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center,
            ) {
                if (!favorite.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = favorite.imageUrl,
                        contentDescription = favorite.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(text = "📦", fontSize = 30.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Nom du produit
                Text(
                    text = favorite.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                )

                // Marque
                if (favorite.brand.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = favorite.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // NutriScore
                if (score != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(nutriScoreColor(score)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = score.uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = nutriScoreLabel(score),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = nutriScoreColor(score),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formatDate(favorite.favoriteAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFBDBDBD),
                )
            }

            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Retirer des favoris",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}