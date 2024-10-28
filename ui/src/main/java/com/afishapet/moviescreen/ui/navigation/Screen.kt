package com.afishapet.moviescreen.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Cinemas : Screen

    @Serializable
    data class Movies(val cinemaId: String) : Screen

    @Serializable
    data class MovieInfo(val cinemaId: String, val movieId: String) : Screen

    @Serializable
    data class Booking(val cinemaId: String, val movieId: String, val eventId: String) : Screen
}
