package com.afishapet.moviescreen.data.di

import com.afishapet.moviescreen.data.repository.ConnectivityRepositoryImpl
import com.afishapet.moviescreen.domain.repository.ConnectivityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ConnectivityModule {
    @Binds
    fun bindConnectivityRepository(
        connectivityRepository: ConnectivityRepositoryImpl
    ): ConnectivityRepository
}