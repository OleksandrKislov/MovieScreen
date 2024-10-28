package com.afishapet.moviescreen.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.afishapet.moviescreen.ui.R
import java.net.UnknownHostException

@Composable
fun BaseErrorHandler(
    exception: Throwable,
    confirmAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (exception is UnknownHostException)
        ErrorDialog(
            modifier = modifier,
            title = stringResource(R.string.network_problem_title),
            text = stringResource(R.string.network_problem_text),
            icon = Icons.Default.Warning,
            confirmButtonText = stringResource(R.string.retry),
            onConfirmClick = confirmAction
        )
    else
        ErrorDialog(
            modifier = modifier,
            title = stringResource(R.string.oops),
            text = stringResource(R.string.something_went_wrong),
            icon = Icons.Default.Warning,
            confirmButtonText = stringResource(R.string.retry),
            onConfirmClick = confirmAction
        )
}