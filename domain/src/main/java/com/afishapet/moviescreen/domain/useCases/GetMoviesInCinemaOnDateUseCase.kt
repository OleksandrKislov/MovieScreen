package com.afishapet.moviescreen.domain.useCases

import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Movie
import com.afishapet.moviescreen.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetMoviesInCinemaOnDateUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository
) {
    operator fun invoke(date: String, cinemaId: String): Flow<Answer<List<Movie>>> =
        moviesRepository.getMoviesInCinemaOnDate(date = date, cinemaId = cinemaId)
            .catch {
                emit(Answer.Error(it))
            }
            .distinctUntilChanged()
}