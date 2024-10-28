package com.afishapet.moviescreen.data.datasource

import com.afishapet.moviescreen.domain.models.MovieInfo

interface MovieInfoRemoteDataSource {
    suspend fun getMovieInfoInCinema(movieId: String, cinemaId: String): MovieInfo

    suspend fun getMovieBookingUrl(cinemaId: String, movieId: String, eventId: String): String
}