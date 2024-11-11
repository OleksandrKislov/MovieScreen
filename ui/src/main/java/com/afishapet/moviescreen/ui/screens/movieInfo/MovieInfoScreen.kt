package com.afishapet.moviescreen.ui.screens.movieInfo

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.afishapet.moviescreen.ui.R
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.ui.components.AppCircularProgressIndicator
import com.afishapet.moviescreen.ui.components.BaseErrorHandler
import com.afishapet.moviescreen.ui.components.ExpandableCard
import com.afishapet.moviescreen.ui.utils.LocalTopAppBarState

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun SharedTransitionScope.MovieInfoScreen(
    state: MovieInfoViewModel.ScreenState,
    onEvent: (MovieInfoViewModel.UiEvent) -> Unit,
    navigateToBookingScreen: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    with(LocalTopAppBarState.current) {
        LaunchedEffect(state.movieName) {
            titleText.value = state.movieName
            actions.value = {}
        }
    }

    val scheduleLazyListState = rememberLazyListState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .sharedElement(
                    state = rememberSharedContentState(
                        key = "image-${state.posterImageUrl}"
                    ),
                    renderInOverlayDuringTransition = false,
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            colorFilter = ColorFilter.tint(
                Color.Black.copy(alpha = 0.6f),
                blendMode = BlendMode.Darken
            ),
            model = state.posterImageUrl,
            contentDescription = state.movieName,
            contentScale = ContentScale.FillBounds
        )

        when (state.movieInfoAnswer) {
            is Answer.Loading -> AppCircularProgressIndicator()

            is Answer.Error -> BaseErrorHandler(
                modifier = Modifier.padding(5.dp),
                exception = state.movieInfoAnswer.exception,
                confirmAction = { onEvent(MovieInfoViewModel.UiEvent.RetryFetchData) }
            )

            is Answer.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    ExpandableCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        isExpanded = state.isDescriptionVisible,
                        toggleIsExpanded = {
                            onEvent(MovieInfoViewModel.UiEvent.ToggleIsDescriptionVisible)
                        },
                        content = {
                            Text(
                                text = stringResource(R.string.info),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        expandedContent = {
                            state.movieInfoAnswer.data.description.forEach {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    )

                    val defaultElevatedCardShape = CardDefaults.elevatedShape as RoundedCornerShape
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 5.dp)
                            .clip(
                                defaultElevatedCardShape.copy(
                                    bottomEnd = CornerSize(0.dp),
                                    bottomStart = CornerSize(0.dp)
                                )
                            ),
                        state = scheduleLazyListState,
                        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                        contentPadding = PaddingValues(top = 5.dp, bottom = 5.dp)
                    ) {
                        state.movieInfoAnswer.data.schedule.forEach { schedule ->
                            stickyHeader {
                                ElevatedCard(
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                    )
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        text = schedule.date,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            item {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        10.dp,
                                        Alignment.Start
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
                                ) {
                                    schedule.sessions.forEach { session ->
                                        ElevatedCard(
                                            modifier = Modifier.width(intrinsicSize = IntrinsicSize.Max),
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = MaterialTheme.colorScheme.secondary,
                                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                            ),
                                            onClick = { navigateToBookingScreen(session.id) }
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(5.dp),
                                                text = session.time,
                                                style = MaterialTheme.typography.bodyLarge,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(5.dp),
                                                text = session.price,
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
    }
}