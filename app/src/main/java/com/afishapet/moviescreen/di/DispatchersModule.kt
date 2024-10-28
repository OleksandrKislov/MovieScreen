package com.afishapet.moviescreen.di

import com.afishapet.moviescreen.dispatchers.DefaultDispatchersProvider
import com.afishapet.moviescreen.domain.dispatchers.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DispatchersModule {
    @Binds
    fun bindDispatchersProvider(
        dispatchersProvider: DefaultDispatchersProvider
    ): DispatchersProvider
}