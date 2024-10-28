package com.afishapet.moviescreen.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface ConnectivityRepository {
    val isConnected: StateFlow<Boolean>
}