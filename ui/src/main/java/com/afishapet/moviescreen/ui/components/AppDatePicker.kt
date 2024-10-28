package com.afishapet.moviescreen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afishapet.moviescreen.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePicker(
    date: String,
    setNewDate: (Long) -> Unit
) {
    var isDatePickerVisible by rememberSaveable { mutableStateOf(value = false) }

    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { isDatePickerVisible = true }
            .padding(horizontal = 5.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(R.string.date_picker),
            )

            Text(
                text = date,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }

    val datePickerState = rememberDatePickerState()
    if (isDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { isDatePickerVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        setNewDate(datePickerState.selectedDateMillis!!)
                        isDatePickerVisible = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isDatePickerVisible = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                headline = null,
                title = null
            )
        }
    }
}