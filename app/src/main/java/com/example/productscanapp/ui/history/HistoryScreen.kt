package com.example.productscanapp.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ScanHistoryItem
import java.text.DateFormat
import java.util.Date

private val HistoryItemShape = RoundedCornerShape(18.dp)

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

@Composable
fun HistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel,
    onProductClick: (Product) -> Unit,
) {
    val history by viewModel.history.collectAsState()
    HistoryScreen(
        items = history,
        onProductClick = onProductClick,
        onDeleteConfirm = { barcode -> viewModel.deleteFromHistory(barcode) },
        modifier = modifier,
    )
}

@Composable
fun HistoryScreen(
    items: List<ScanHistoryItem>,
    onProductClick: (Product) -> Unit,
    onDeleteConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pendingDeletion = remember { mutableStateOf<ScanHistoryItem?>(null) }

    // Dialogue de confirmation de suppression
    val itemToDelete = pendingDeletion.value
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeletion.value = null },
            title = { Text("Supprimer ce produit ?") },
            text = { Text("\"${itemToDelete.product.name}\" sera retiré de l'historique.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteConfirm(itemToDelete.product.barcode)
                    pendingDeletion.value = null
                }) {
                    Text("Supprimer", color = Color(0xFFB00020))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeletion.value = null }) {
                    Text("Annuler")
                }
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = "Historique",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Retrouve tous tes scans récents",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aucun produit scanné",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Les produits scannés apparaîtront ici",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    )
                }
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items, key = { it.product.barcode }) { item ->
                SwipeToDeleteHistoryItem(
                    item = item,
                    onClick = { onProductClick(item.product) },
                    onDeleteRequested = { pendingDeletion.value = item },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteHistoryItem(
    item: ScanHistoryItem,
    onClick: () -> Unit,
    onDeleteRequested: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteRequested()
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(HistoryItemShape)
                    .background(Color(0xFFB00020))
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Supprimer",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        },
        content = {
            HistoryItem(item = item, onClick = onClick)
        },
    )
}

@Composable
private fun HistoryItem(
    item: ScanHistoryItem,
    onClick: () -> Unit,
) {
    val score = item.product.nutriScore

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = HistoryItemShape)
            .clickable(onClick = onClick),
        shape = HistoryItemShape,
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
                if (!item.product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(text = "📦", fontSize = 30.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                )

                if (item.product.brand.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.product.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

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
                    text = formatDate(item.scannedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFBDBDBD),
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFE0E0E0),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private fun formatDate(timeMillis: Long): String {
    val date = Date(timeMillis)
    val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, java.util.Locale.FRANCE)
    val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, java.util.Locale.FRANCE)
    return "${dateFormatter.format(date)} à ${timeFormatter.format(date)}"
}


