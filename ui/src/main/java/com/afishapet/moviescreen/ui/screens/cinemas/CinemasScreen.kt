package com.afishapet.moviescreen.ui.screens.cinemas

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.SubcomposeAsyncImage
import com.afishapet.moviescreen.ui.R
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.ui.components.AppCircularProgressIndicator
import com.afishapet.moviescreen.ui.components.BaseErrorHandler
import com.afishapet.moviescreen.ui.utils.LocalTopAppBarState

@Composable
fun CinemasScreen(
    state: CinemasViewModel.ScreenState,
    onEvent: (CinemasViewModel.UiEvent) -> Unit,
    navigateToMovieListScreen: (String) -> Unit,
) {
    val scaffoldTitle = stringResource(R.string.app_name)
    with(LocalTopAppBarState.current) {
        LaunchedEffect(Unit) {
            titleText.value = scaffoldTitle
            actions.value = {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state.cinemasAnswer) {
            is Answer.Loading -> AppCircularProgressIndicator()

            is Answer.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
            ) {
                items(state.cinemasAnswer.data) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = { navigateToMovieListScreen(it.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SubcomposeAsyncImage(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                model = it.logoImageUrl,
                                contentDescription = it.name,
                                loading = {
                                    Box(contentAlignment = Alignment.Center) {
                                        AppCircularProgressIndicator()
                                    }
                                }
                            )
                            Text(
                                modifier = Modifier.weight(5f),
                                text = it.name,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            val ctx = LocalContext.current
                            IconButton(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.google.com/maps/search/?api=1&query=${it.address}")
                                    )
                                    startActivity(ctx, intent, null)
                                }
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(0.5f),
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "maps"
                                )
                            }
                        }
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = it.address,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is Answer.Error -> BaseErrorHandler(
                modifier = Modifier.padding(5.dp),
                exception = state.cinemasAnswer.exception,
                confirmAction = { onEvent(CinemasViewModel.UiEvent.RetryFetchCinemas) }
            )
        }
    }
}