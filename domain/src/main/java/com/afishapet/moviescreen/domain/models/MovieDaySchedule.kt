package com.afishapet.moviescreen.domain.models

data class MovieDaySchedule(
    val date: String,
    val sessions: List<Session>
)