package com.example.productscanapp.ui.common

import androidx.compose.ui.graphics.Color

fun String?.toNutriScoreColor(): Color {
    return when (this?.uppercase()) {
        "A" -> Color(0xFF2E7D32)
        "B" -> Color(0xFF7CB342)
        "C" -> Color(0xFFFBC02D)
        "D" -> Color(0xFFFB8C00)
        "E" -> Color(0xFFC62828)
        else -> Color.Gray
    }
}