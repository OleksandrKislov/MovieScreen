package com.afishapet.moviescreen.domain.repository

import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {

    fun getMoviesInCinemaOnDate(date: String, cinemaId: String): Flow<Answer<List<Movie>>>

    fun getMovieById(movieId: String): Flow<Answer<Movie>>

}