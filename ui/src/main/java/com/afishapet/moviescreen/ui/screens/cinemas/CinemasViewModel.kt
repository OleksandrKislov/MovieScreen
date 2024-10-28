package com.afishapet.moviescreen.ui.screens.cinemas

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Cinema
import com.afishapet.moviescreen.domain.useCases.GetCinemasUseCase
import com.afishapet.moviescreen.ui.utils.RetryChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CinemasViewModel @Inject constructor(
    private val getCinemasUseCase: GetCinemasUseCase
) : ViewModel() {

    @Immutable
    data class ScreenState(
        val cinemasAnswer: Answer<List<Cinema>> = Answer.Loading,
    )

    sealed class UiEvent {
        data object RetryFetchCinemas : UiEvent()
    }

    private val retryFetchCinemasChannel = RetryChannel()

    private val cinemasAnswer: Flow<Answer<List<Cinema>>> = retryFetchCinemasChannel.flow.flatMapLatest {
            getCinemasUseCase()
        }

    val state: StateFlow<ScreenState> = cinemasAnswer
        .mapLatest { ScreenState(cinemasAnswer = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState())

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.RetryFetchCinemas -> fetchCinemas()
        }
    }

    private fun fetchCinemas() {
        viewModelScope.launch {
            retryFetchCinemasChannel.retry()
        }
    }
}