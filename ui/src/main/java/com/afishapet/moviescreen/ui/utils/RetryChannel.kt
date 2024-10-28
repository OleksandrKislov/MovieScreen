package com.afishapet.moviescreen.ui.utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow

class RetryChannel {

    private val channel = Channel<Unit>(Channel.CONFLATED)

    val flow = channel.receiveAsFlow().onStart { emit(Unit) }

    suspend fun retry() = channel.send(Unit)
}