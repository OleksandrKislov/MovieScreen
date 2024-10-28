package com.afishapet.moviescreen.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.afishapet.moviescreen.ui.screens.booking.BookingScreen
import com.afishapet.moviescreen.ui.screens.booking.BookingViewModel
import com.afishapet.moviescreen.ui.screens.cinemas.CinemasScreen
import com.afishapet.moviescreen.ui.screens.cinemas.CinemasViewModel
import com.afishapet.moviescreen.ui.screens.movieInfo.MovieInfoScreen
import com.afishapet.moviescreen.ui.screens.movieInfo.MovieInfoViewModel
import com.afishapet.moviescreen.ui.screens.movies.MoviesScreen
import com.afishapet.moviescreen.ui.screens.movies.MoviesViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavRoot(
    modifier: Modifier,
    navController: NavHostController
) {
    SharedTransitionLayout(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = Screen.Cinemas,
            ) {
                composable<Screen.Cinemas> {
                    val cinemaListViewModel = hiltViewModel<CinemasViewModel>()

                    CinemasScreen(
                        state = cinemaListViewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = cinemaListViewModel::onEvent,
                        navigateToMovieListScreen = { cinemaId ->
                            navController.navigate(Screen.Movies(cinemaId = cinemaId)) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable<Screen.Movies> {
                    val args = it.toRoute<Screen.Movies>()

                    val moviesViewModel =
                        hiltViewModel<MoviesViewModel, MoviesViewModel.Factory>(
                            creationCallback = { factory ->
                                factory.create(cinemaId = args.cinemaId)
                            }
                        )

                    MoviesScreen(
                        state = moviesViewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = moviesViewModel::onEvent,
                        navigateToMovieInfoScreen = { movieId ->
                            navController.navigate(
                                Screen.MovieInfo(
                                    movieId = movieId,
                                    cinemaId = args.cinemaId
                                )
                            ) {
                                launchSingleTop = true
                            }
                        },
                        animatedVisibilityScope = this@composable
                    )
                }

                composable<Screen.MovieInfo> {
                    val args = it.toRoute<Screen.MovieInfo>()

                    val movieInfoViewModel =
                        hiltViewModel<MovieInfoViewModel, MovieInfoViewModel.Factory>(
                            creationCallback = { factory ->
                                factory.create(movieId = args.movieId, cinemaId = args.cinemaId)
                            }
                        )

                    MovieInfoScreen(
                        state = movieInfoViewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = movieInfoViewModel::onEvent,
                        navigateToBookingScreen = { eventId ->
                            navController.navigate(
                                Screen.Booking(
                                    eventId = eventId,
                                    movieId = args.movieId,
                                    cinemaId = args.cinemaId
                                )
                            ) {
                                launchSingleTop = true
                            }
                        },
                        animatedVisibilityScope = this@composable
                    )
                }

                composable<Screen.Booking> {
                    val args = it.toRoute<Screen.Booking>()

                    val bookingViewModel =
                        hiltViewModel<BookingViewModel, BookingViewModel.Factory>(
                            creationCallback = { factory ->
                                factory.create(
                                    movieId = args.movieId,
                                    eventId = args.eventId,
                                    cinemaId = args.cinemaId
                                )
                            }
                        )

                    BookingScreen(
                        state = bookingViewModel.state.collectAsStateWithLifecycle().value,
                        onEvent = bookingViewModel::onEvent,
                    )
                }
            }
        }
    }
}