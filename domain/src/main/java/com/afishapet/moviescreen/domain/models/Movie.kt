package com.afishapet.moviescreen.domain.models

data class Movie(
    val id: String,
    val name: String,
    val genre: String,
    val cost: String,
    val posterImageUrl: String,
    val cinemaIds: List<String>
)