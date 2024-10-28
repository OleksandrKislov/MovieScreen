package com.afishapet.moviescreen.ui.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

data class TopAppBarState(
    val titleText: MutableState<String> = mutableStateOf(""),
    val actions: MutableState<@Composable RowScope.() -> Unit> = mutableStateOf({}),
)
val LocalTopAppBarState = compositionLocalOf { TopAppBarState() }