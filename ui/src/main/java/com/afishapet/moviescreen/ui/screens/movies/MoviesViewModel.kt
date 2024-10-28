package com.afishapet.moviescreen.ui.screens.movies

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Cinema
import com.afishapet.moviescreen.domain.models.Movie
import com.afishapet.moviescreen.domain.useCases.GetCinemaByIdUseCase
import com.afishapet.moviescreen.domain.useCases.GetMoviesInCinemaOnDateUseCase
import com.afishapet.moviescreen.ui.utils.RetryChannel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = MoviesViewModel.Factory::class)
class MoviesViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMoviesInCinemaOnDateUseCase: GetMoviesInCinemaOnDateUseCase,
    getCinemaByIdUseCase: GetCinemaByIdUseCase,
    @Assisted private val cinemaId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(cinemaId: String): MoviesViewModel
    }

    @Immutable
    data class ScreenState(
        val moviesAnswer: Answer<List<Movie>> = Answer.Loading,
        val date: String,
        val cinemaName: String,
        val cinemaId: String
    )

    sealed class UiEvent {
        data object RetryFetchMovies : UiEvent()
        data class SetNewDate(val newDateTimestamp: Long) : UiEvent()
    }

    private val cinemaAnswer: Flow<Answer<Cinema>> = getCinemaByIdUseCase(cinemaId)

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val dateFlow: StateFlow<String> =
        savedStateHandle.getStateFlow("date", LocalDate.now().format(dateFormatter))

    private val retryFetchMoviesChannel = RetryChannel()

    private val moviesAnswer: Flow<Answer<List<Movie>>> =
        combine(dateFlow, retryFetchMoviesChannel.flow) { date, _ ->
            getMoviesInCinemaOnDateUseCase(date = date, cinemaId = cinemaId)
        }.flattenMerge()

    val state: StateFlow<ScreenState> =
        combine(dateFlow, moviesAnswer, cinemaAnswer) { date, movies, cinema ->
            ScreenState(
                date = date,
                moviesAnswer = movies,
                cinemaId = cinemaId,
                cinemaName = when (cinema) {
                    is Answer.Success -> cinema.data.name
                    else -> ""
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ScreenState(
                date = LocalDate.now().format(dateFormatter),
                cinemaId = cinemaId,
                cinemaName = ""
            )
        )

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.RetryFetchMovies -> retryFetchMovies()
            is UiEvent.SetNewDate -> setNewDate(newDateTimestamp = event.newDateTimestamp)
        }
    }

    private fun setNewDate(newDateTimestamp: Long) {
        val newDate = Instant.ofEpochMilli(newDateTimestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(dateFormatter)

        savedStateHandle["date"] = newDate
    }

    private fun retryFetchMovies() {
        viewModelScope.launch {
            retryFetchMoviesChannel.retry()
        }
    }
}