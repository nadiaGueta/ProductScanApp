package com.example.productscanapp.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ScanHistoryItem
import java.text.DateFormat
import java.util.Date

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
        modifier = modifier,
    )
}

@Composable
fun HistoryScreen(
    items: List<ScanHistoryItem>,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Aucun produit scanné")
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, key = { it.product.barcode }) { item ->
            HistoryItem(
                item = item,
                onClick = { onProductClick(item.product) },
            )
        }
    }
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

@Composable
private fun formatDate(timeMillis: Long): String {
    val date = Date(timeMillis)
    val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, java.util.Locale.FRANCE)
    val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, java.util.Locale.FRANCE)
    return "Le ${dateFormatter.format(date)} à ${timeFormatter.format(date)}"
}


