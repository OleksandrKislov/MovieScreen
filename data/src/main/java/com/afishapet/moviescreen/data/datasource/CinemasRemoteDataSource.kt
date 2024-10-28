package com.afishapet.moviescreen.data.datasource

import com.afishapet.moviescreen.domain.models.Cinema

interface CinemasRemoteDataSource {
    suspend fun getCinemas(): List<Cinema>

    suspend fun getCinemaById(id: String): Cinema
}