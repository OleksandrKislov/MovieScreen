package com.afishapet.moviescreen.data.di

import com.afishapet.moviescreen.data.datasource.CinemasRemoteDataSource
import com.afishapet.moviescreen.data.datasource.megakino.CinemasMegakinoDataSource
import com.afishapet.moviescreen.data.repository.CinemasRepositoryImpl
import com.afishapet.moviescreen.domain.repository.CinemasRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CinemasModule {
    @Binds
    fun bindCinemasRepository(
        cinemasRepository: CinemasRepositoryImpl
    ): CinemasRepository

    @Binds
    fun bindCinemasRemoteDataSource(
        cinemasRemoteDataSource: CinemasMegakinoDataSource
    ): CinemasRemoteDataSource
}