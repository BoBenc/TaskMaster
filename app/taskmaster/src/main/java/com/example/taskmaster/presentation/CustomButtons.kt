package com.example.taskmaster.presentation

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults.buttonColors
import androidx.wear.compose.material.Text

@Composable
fun DoneButton(onClick: () -> Unit, text: String = "✅") {
    Button (
        onClick = onClick,
        modifier = Modifier,
        shape = CircleShape,
        colors = buttonColors(
            backgroundColor = Color(0xFF00C107)
        )
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun RefreshButton(onClick: () -> Unit, text: String = "🔁") {
    Button (
        onClick = onClick,
        modifier = Modifier,
        shape = CircleShape,
        colors = buttonColors(
            backgroundColor = Color(0xFFF8B000)
        )
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun DeleteButton(onClick: () -> Unit, text: String = "❌") {
    Button (
        onClick = onClick,
        modifier = Modifier,
        shape = CircleShape,
        colors = buttonColors(
            backgroundColor = Color(0xFFFF0000)
        )
    ) {
        Text(text, color = Color.White)
    }
}