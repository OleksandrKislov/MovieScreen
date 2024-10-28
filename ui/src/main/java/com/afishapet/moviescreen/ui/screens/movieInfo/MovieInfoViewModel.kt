package com.afishapet.moviescreen.ui.screens.movieInfo

import androidx.compose.runtime.Immutable
import androidx.lifecycle.*
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.MovieInfo
import com.afishapet.moviescreen.domain.useCases.GetMovieByIdUseCase
import com.afishapet.moviescreen.domain.useCases.GetMovieInfoUseCase
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = MovieInfoViewModel.Factory::class)
class MovieInfoViewModel @AssistedInject constructor(
    @Assisted("movieId") private val movieId: String,
    @Assisted("cinemaId") private val cinemaId: String,
    private val savedStateHandle: SavedStateHandle,
    private val getMovieInfoUseCase: GetMovieInfoUseCase,
    getMovieByIdUseCase: GetMovieByIdUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("movieId") movieId: String,
            @Assisted("cinemaId") cinemaId: String
        ): MovieInfoViewModel
    }

    @Immutable
    data class ScreenState(
        val movieInfoAnswer: Answer<MovieInfo> = Answer.Loading,
        val isDescriptionVisible: Boolean = false,
        val movieName: String = "",
        val posterImageUrl: String = "",
    )

    sealed class UiEvent {
        data object RetryFetchData : UiEvent()
        data object ToggleIsDescriptionVisible : UiEvent()
    }

    private val movieAnswer = getMovieByIdUseCase(movieId)

    private val retryChannel = RetryChannel()

    private val movieInfoAnswer: Flow<Answer<MovieInfo>> = retryChannel.flow.flatMapLatest {
        getMovieInfoUseCase(movieId = movieId, cinemaId = cinemaId)
    }

    private val isDescriptionVisible = savedStateHandle.getStateFlow("isDescriptionVisible", false)

    val state: StateFlow<ScreenState> =
        combine(
            movieInfoAnswer,
            isDescriptionVisible,
            movieAnswer
        ) { movieInfo, isDescVisible, movie ->
            ScreenState(
                movieInfoAnswer = movieInfo,
                isDescriptionVisible = isDescVisible,
                movieName = when (movie) {
                    is Answer.Success -> movie.data.name
                    else -> ""
                },
                posterImageUrl = when (movie) {
                    is Answer.Success -> movie.data.posterImageUrl
                    else -> ""
                },
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ScreenState()
        )

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.RetryFetchData -> retryFetchData()
            is UiEvent.ToggleIsDescriptionVisible -> toggleIsDescriptionVisible()
        }
    }

    private fun toggleIsDescriptionVisible() {
        savedStateHandle["isDescriptionVisible"] = !isDescriptionVisible.value
    }

    private fun retryFetchData() {
        viewModelScope.launch {
            retryChannel.retry()
        }
    }
}