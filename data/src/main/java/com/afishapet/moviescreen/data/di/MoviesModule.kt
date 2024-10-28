package com.afishapet.moviescreen.data.di

import com.afishapet.moviescreen.data.datasource.MoviesRemoteDataSource
import com.afishapet.moviescreen.data.datasource.megakino.MoviesMegakinoDataSource
import com.afishapet.moviescreen.data.repository.MoviesRepositoryImpl
import com.afishapet.moviescreen.domain.repository.MoviesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MoviesModule {
    @Binds
    fun bindMoviesRepository(
        moviesRepository: MoviesRepositoryImpl
    ): MoviesRepository

    @Binds
    fun bingMoviesRemoteDataSource(
        moviesRemoteDataSource: MoviesMegakinoDataSource
    ): MoviesRemoteDataSource
}