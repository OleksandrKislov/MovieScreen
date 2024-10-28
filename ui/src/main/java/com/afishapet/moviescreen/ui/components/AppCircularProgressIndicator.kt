package com.afishapet.moviescreen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun AppCircularProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.primary),
        color = MaterialTheme.colorScheme.onPrimary
    )
}