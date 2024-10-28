package com.afishapet.moviescreen.data.di

import com.afishapet.moviescreen.data.datasource.MovieInfoRemoteDataSource
import com.afishapet.moviescreen.data.datasource.megakino.MovieInfoMegakinoDataSource
import com.afishapet.moviescreen.data.repository.MovieInfoRepositoryImpl
import com.afishapet.moviescreen.domain.repository.MovieInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MovieInfoModule {
    @Binds
    fun bindMovieInfoRepository(
        movieInfoRepository: MovieInfoRepositoryImpl
    ): MovieInfoRepository

    @Binds
    fun bingMovieInfoRemoteDataSource(
        movieInfoRemoteDataSource: MovieInfoMegakinoDataSource
    ): MovieInfoRemoteDataSource
}