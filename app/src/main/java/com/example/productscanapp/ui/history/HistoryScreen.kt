package com.example.productscanapp.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ScanHistoryItem
import java.text.DateFormat
import java.util.Date

private val HistoryItemShape = RoundedCornerShape(12.dp)

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
    var pendingDeletion by remember { mutableStateOf<ScanHistoryItem?>(null) }

    if (items.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Aucun produit scanné")
        }
        return
    }

    val itemToDelete = pendingDeletion
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDeletion = null },
            title = { Text("Supprimer ce produit ?") },
            text = {
                Text("Ce produit sera retiré de l'historique.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteConfirm(itemToDelete.product.barcode)
                        pendingDeletion = null
                    },
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeletion = null }) {
                    Text("Annuler")
                }
            },
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, key = { it.product.barcode }) { item ->
            SwipeToDeleteHistoryItem(
                item = item,
                onClick = { onProductClick(item.product) },
                onDeleteRequested = { pendingDeletion = item },
            )
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
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteRequested()
                false
            } else {
                true
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(HistoryItemShape)
                    .background(Color(0xFFB00020))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = Color.White,
                )
            }
        },
        content = {
            HistoryItem(
                item = item,
                onClick = onClick,
            )
        },
    )
}

@Composable
private fun HistoryItem(
    item: ScanHistoryItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = HistoryItemShape,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = item.product.brand)
            Text(text = "Code-barres: ${item.product.barcode}")
            
            Text(
                text = formatDate(item.scannedAt),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

private fun formatDate(timeMillis: Long): String {
    val date = Date(timeMillis)
    val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, java.util.Locale.FRANCE)
    val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, java.util.Locale.FRANCE)
    return "Le ${dateFormatter.format(date)} à ${timeFormatter.format(date)}"
}


