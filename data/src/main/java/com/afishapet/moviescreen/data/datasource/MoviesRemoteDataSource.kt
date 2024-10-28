package com.afishapet.moviescreen.data.datasource

import com.afishapet.moviescreen.domain.models.Movie

interface MoviesRemoteDataSource {
    suspend fun getAllMovies(): List<Movie>

    suspend fun getMoviesInCinemaOnDate(date: String, cinemaId: String): List<Movie>

    suspend fun getMovieById(movieId: String): Movie
}