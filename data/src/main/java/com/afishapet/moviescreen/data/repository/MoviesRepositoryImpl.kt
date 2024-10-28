package com.afishapet.moviescreen.data.repository

import com.afishapet.moviescreen.data.datasource.MoviesRemoteDataSource
import com.afishapet.moviescreen.data.datasource.requestAnswer
import com.afishapet.moviescreen.domain.dispatchers.DispatchersProvider
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Movie
import com.afishapet.moviescreen.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepositoryImpl @Inject constructor(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val dispatchers: DispatchersProvider
) : MoviesRepository {
    override fun getMoviesInCinemaOnDate(
        date: String,
        cinemaId: String
    ): Flow<Answer<List<Movie>>> = requestAnswer(dispatchers.io) {
        moviesRemoteDataSource.getMoviesInCinemaOnDate(date, cinemaId)
    }

    override fun getMovieById(movieId: String): Flow<Answer<Movie>> =
        requestAnswer(dispatchers.io) {
            moviesRemoteDataSource.getMovieById(movieId)
        }
}