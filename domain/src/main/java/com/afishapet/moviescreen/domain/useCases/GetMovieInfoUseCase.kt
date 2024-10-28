package com.afishapet.moviescreen.domain.useCases

import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.MovieInfo
import com.afishapet.moviescreen.domain.repository.MovieInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetMovieInfoUseCase @Inject constructor(
    private val movieInfoRepository: MovieInfoRepository
) {
    operator fun invoke(movieId: String, cinemaId: String): Flow<Answer<MovieInfo>> =
        movieInfoRepository.getMovieInfo(movieId = movieId, cinemaId = cinemaId)
            .catch {
                emit(Answer.Error(it))
            }
            .distinctUntilChanged()
}