package com.afishapet.moviescreen.domain.useCases

import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.repository.MovieInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetMovieBookingUrlUseCase @Inject constructor(
    private val movieInfoRepository: MovieInfoRepository
) {
    operator fun invoke(
        cinemaId: String,
        movieId: String,
        eventId: String
    ): Flow<Answer<String>> =
        movieInfoRepository.getMovieBookingUrl(
            cinemaId = cinemaId,
            movieId = movieId,
            eventId = eventId
        ).catch {
            emit(Answer.Error(it))
        }.distinctUntilChanged()
}