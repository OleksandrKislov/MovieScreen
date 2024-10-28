package com.afishapet.moviescreen.ui.screens.booking

import android.webkit.WebResourceError
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.useCases.GetMovieBookingUrlUseCase
import com.afishapet.moviescreen.domain.useCases.GetMovieByIdUseCase
import com.afishapet.moviescreen.ui.utils.RetryChannel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = BookingViewModel.Factory::class)
class BookingViewModel @AssistedInject constructor(
    @Assisted("movieId") private val movieId: String,
    @Assisted("eventId") private val eventId: String,
    @Assisted("cinemaId") private val cinemaId: String,
    getMovieByIdUseCase: GetMovieByIdUseCase,
    getMovieBookingUrlUseCase: GetMovieBookingUrlUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("movieId") movieId: String,
            @Assisted("eventId") eventId: String,
            @Assisted("cinemaId") cinemaId: String,
        ): BookingViewModel
    }

    @Immutable
    data class ScreenState(
        val bookingUrlAnswer: Answer<String> = Answer.Loading,
        val movieName: String = "",
        val posterImageUrl: String = "",
        val isLoadingWebView: Boolean = true,
        val isEventAvailable: Boolean = true,
        val webResourceError: WebResourceError? = null
    )

    sealed class UiEvent {
        data object RetryGenerateMovieBookingUrl : UiEvent()
        data object WebViewLoaded : UiEvent()
        data object EventUnavailable : UiEvent()
        data class UpdateWebResourceError(val error: WebResourceError?) : UiEvent()
    }

    private val retryChannel = RetryChannel()

    private val bookingUrlAnswer = retryChannel.flow.flatMapLatest {
        getMovieBookingUrlUseCase(cinemaId = cinemaId, movieId = movieId, eventId = eventId)
    }

    private val movieAnswer = getMovieByIdUseCase(movieId)

    private val isLoadingWebView = MutableStateFlow(true)

    private val isEventAvailable = MutableStateFlow(true)

    private val webResourceError = MutableStateFlow<WebResourceError?>(null)

    val state: StateFlow<ScreenState> =
        combine(
            bookingUrlAnswer,
            movieAnswer,
            isLoadingWebView,
            isEventAvailable,
            webResourceError
        ) { bookingUrl, movie, isLoading, isEventAvailable, webResourceError ->
            ScreenState(
                bookingUrlAnswer = bookingUrl,
                movieName = when (movie) {
                    is Answer.Success -> movie.data.name
                    else -> ""
                },
                posterImageUrl = when (movie) {
                    is Answer.Success -> movie.data.posterImageUrl
                    else -> ""
                },
                isLoadingWebView = isLoading,
                isEventAvailable = isEventAvailable,
                webResourceError = webResourceError
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ScreenState()
        )

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.RetryGenerateMovieBookingUrl -> retryGenerateMovieBookingUrl()
            is UiEvent.WebViewLoaded -> webViewLoaded()
            is UiEvent.EventUnavailable -> eventUnavailable()
            is UiEvent.UpdateWebResourceError -> updateWebResourceError(event.error)
        }
    }

    private fun retryGenerateMovieBookingUrl() {
        viewModelScope.launch {
            retryChannel.retry()
        }
    }

    private fun webViewLoaded() {
        isLoadingWebView.update { false }
    }

    private fun eventUnavailable() {
        isEventAvailable.update { false }
    }

    private fun updateWebResourceError(error: WebResourceError?) {
        webResourceError.update { error }
    }
}