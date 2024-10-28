package com.afishapet.moviescreen.domain.repository

import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.MovieInfo
import kotlinx.coroutines.flow.Flow

interface MovieInfoRepository {

    fun getMovieInfo(movieId: String, cinemaId: String): Flow<Answer<MovieInfo>>

    fun getMovieBookingUrl(cinemaId: String, movieId: String, eventId: String): Flow<Answer<String>>

}