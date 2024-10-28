package com.afishapet.moviescreen.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.let

@Composable
fun ErrorDialog(
    text: String,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        icon?.let {
            Icon(
                modifier = Modifier
                    .align(Alignment.Companion.CenterHorizontally)
                    .padding(vertical = 15.dp)
                    .size(40.dp),
                imageVector = icon,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = null
            )
        }
        title?.let {
            Text(
                modifier = Modifier
                    .align(Alignment.Companion.CenterHorizontally)
                    .padding(bottom = 15.dp, start = 15.dp, end = 15.dp),
                text = it,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

        }

        Text(
            modifier = Modifier
                .align(Alignment.Companion.CenterHorizontally)
                .padding(horizontal = 15.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            modifier = Modifier
                .align(Alignment.Companion.End)
                .padding(bottom = 15.dp, top = 15.dp, end = 15.dp),
            onClick = onConfirmClick
        ) {
            Text(text = confirmButtonText)
        }
    }
}