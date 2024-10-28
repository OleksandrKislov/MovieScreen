package com.afishapet.moviescreen.data.repository

import com.afishapet.moviescreen.data.datasource.MovieInfoRemoteDataSource
import com.afishapet.moviescreen.data.datasource.requestAnswer
import com.afishapet.moviescreen.domain.dispatchers.DispatchersProvider
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.MovieInfo
import com.afishapet.moviescreen.domain.repository.MovieInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieInfoRepositoryImpl @Inject constructor(
    private val movieInfoRemoteDataSource: MovieInfoRemoteDataSource,
    private val dispatchers: DispatchersProvider
) : MovieInfoRepository {
    override fun getMovieInfo(movieId: String, cinemaId: String): Flow<Answer<MovieInfo>> =
        requestAnswer(dispatchers.io) {
            movieInfoRemoteDataSource.getMovieInfoInCinema(movieId = movieId, cinemaId = cinemaId)
        }

    override fun getMovieBookingUrl(
        cinemaId: String,
        movieId: String,
        eventId: String,
    ): Flow<Answer<String>> =
        requestAnswer(dispatchers.io) {
            movieInfoRemoteDataSource.getMovieBookingUrl(
                cinemaId = cinemaId,
                movieId = movieId,
                eventId = eventId
            )
        }
}