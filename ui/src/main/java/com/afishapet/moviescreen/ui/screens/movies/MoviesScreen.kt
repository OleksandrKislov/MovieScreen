package com.afishapet.moviescreen.ui.screens.movies

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.ui.R
import com.afishapet.moviescreen.ui.components.AppCircularProgressIndicator
import com.afishapet.moviescreen.ui.components.AppDatePicker
import com.afishapet.moviescreen.ui.components.BaseErrorHandler
import com.afishapet.moviescreen.ui.utils.LocalTopAppBarState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MoviesScreen(
    state: MoviesViewModel.ScreenState,
    onEvent: (MoviesViewModel.UiEvent) -> Unit,
    navigateToMovieInfoScreen: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(LocalTopAppBarState.current) {
        LaunchedEffect(state.date, state.cinemaName) {
            titleText.value = state.cinemaName
            actions.value = {
                AppDatePicker(
                    date = state.date,
                    setNewDate = { onEvent(MoviesViewModel.UiEvent.SetNewDate(it)) }
                )
            }
        }
    }

    val moviesLazyGridState = rememberLazyGridState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state.moviesAnswer) {
            is Answer.Loading -> AppCircularProgressIndicator()

            is Answer.Error -> BaseErrorHandler(
                modifier = Modifier.padding(5.dp),
                exception = state.moviesAnswer.exception,
                confirmAction = { onEvent(MoviesViewModel.UiEvent.RetryFetchMovies) }
            )

            is Answer.Success if state.moviesAnswer.data.isEmpty() -> {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = stringResource(R.string.no_movies_for_this_date),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }

            is Answer.Success -> {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    state = moviesLazyGridState,
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    items(items = state.moviesAnswer.data) { movie ->
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(contentColor = Color.White),
                            onClick = {
                                navigateToMovieInfoScreen(movie.id)
                            }
                        ) {
                            Box(contentAlignment = Alignment.BottomCenter) {
                                AsyncImage(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .sharedElement(
                                            state = rememberSharedContentState(
                                                key = "image-${movie.posterImageUrl}"
                                            ),
                                            animatedVisibilityScope = animatedVisibilityScope,
                                        )
                                        .clip(MaterialTheme.shapes.medium),
                                    model = movie.posterImageUrl,
                                    contentDescription = movie.name,
                                    contentScale = ContentScale.FillWidth,
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colorStops = arrayOf(
                                                    0.0f to Color.Transparent,
                                                    0.2f to Color.Black.copy(alpha = 0.6f),
                                                    0.5f to Color.Black.copy(alpha = 0.8f),
                                                    1.0f to Color.Black.copy(alpha = 0.9f)
                                                )
                                            )
                                        )
                                        .padding(top = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(
                                        space = 2.dp,
                                        alignment = Alignment.Bottom
                                    )
                                ) {
                                    Text(
                                        text = movie.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = movie.cost,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = movie.genre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}