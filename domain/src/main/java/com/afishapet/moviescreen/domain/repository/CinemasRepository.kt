package com.afishapet.moviescreen.domain.repository

import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Cinema
import kotlinx.coroutines.flow.Flow

interface CinemasRepository {
    fun getCinemas(): Flow<Answer<List<Cinema>>>

    fun getCinemaById(id: String): Flow<Answer<Cinema>>
}