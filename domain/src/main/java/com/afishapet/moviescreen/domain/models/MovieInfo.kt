package com.afishapet.moviescreen.domain.models

data class MovieInfo(
    val movieId: String,
    val description: List<String>,
    val schedule: List<MovieDaySchedule>
)