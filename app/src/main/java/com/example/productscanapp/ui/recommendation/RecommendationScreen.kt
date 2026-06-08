package com.example.productscanapp.ui.recommendation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.productscanapp.domain.RecommendationItem
import com.example.productscanapp.ui.common.BetterAlternativeBanner

@Composable
fun RecommendationRoute(
    viewModel: RecommendationViewModel,
    modifier: Modifier = Modifier
) {
    val recommendations by viewModel.recommendations.collectAsState()

    RecommendationScreen(
        recommendations = recommendations,
        modifier = modifier
    )
}

@Composable
fun RecommendationScreen(
    recommendations: List<RecommendationItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recommandations",
            style = MaterialTheme.typography.headlineSmall
        )

        if (recommendations.isEmpty()) {
            Text(
                text = "Aucune recommandation disponible.",
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = recommendations,
                    key = { it.scannedProduct.barcode }
                ) { recommendation ->
                    Text(
                        text = "À la place de ${recommendation.scannedProduct.name}"
                    )

                    BetterAlternativeBanner(
                        product = recommendation.alternative
                    )
                }
            }
        }
    }
}