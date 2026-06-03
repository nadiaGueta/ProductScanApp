package com.example.productscanapp.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == AppTab.Recherche,
            onClick = { onTabSelected(AppTab.Recherche) },
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Produit") }
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.Scanner,
            onClick = { onTabSelected(AppTab.Scanner) },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
            label = { Text("Scanner") }
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.Historique,
            onClick = { onTabSelected(AppTab.Historique) },
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("Historique") }
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.Favoris,
            onClick = { onTabSelected(AppTab.Favoris) },
            icon = { Icon(Icons.Default.Star, contentDescription = null) },
            label = { Text("Favoris") }
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.Reglage,
            onClick = { onTabSelected(AppTab.Reglage) },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Réglage") }
        )
    }
}