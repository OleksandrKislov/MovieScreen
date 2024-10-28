package com.afishapet.moviescreen.data.repository

import com.afishapet.moviescreen.data.datasource.CinemasRemoteDataSource
import com.afishapet.moviescreen.data.datasource.requestAnswer
import com.afishapet.moviescreen.domain.dispatchers.DispatchersProvider
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Cinema
import com.afishapet.moviescreen.domain.repository.CinemasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinemasRepositoryImpl @Inject constructor(
    private val cinemasRemoteDataSource: CinemasRemoteDataSource,
    private val dispatchers: DispatchersProvider
) : CinemasRepository {

    override fun getCinemas(): Flow<Answer<List<Cinema>>> = requestAnswer(dispatchers.io) {
        cinemasRemoteDataSource.getCinemas()
    }

    override fun getCinemaById(id: String): Flow<Answer<Cinema>> = requestAnswer(dispatchers.io) {
        cinemasRemoteDataSource.getCinemaById(id)
    }
}