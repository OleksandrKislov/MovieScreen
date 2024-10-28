package com.afishapet.moviescreen.domain.useCases

import com.afishapet.moviescreen.domain.dispatchers.DispatchersProvider
import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Cinema
import com.afishapet.moviescreen.domain.repository.CinemasRepository
import com.afishapet.moviescreen.domain.repository.ConnectivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import java.net.UnknownHostException
import javax.inject.Inject

class GetCinemaByIdUseCase @Inject constructor(
    private val cinemasRepository: CinemasRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val dispatchers: DispatchersProvider
) {
    operator fun invoke(id: String, autoRetries: Boolean = true): Flow<Answer<Cinema>> = cinemasRepository.getCinemaById(id)
        .retryWhen { cause, attempt ->
            if (autoRetries && cause is UnknownHostException && attempt < 5) {
                connectivityRepository.isConnected
                    .filter { it }
                    .first()
                true
            } else {
                false
            }
        }
        .catch {
            emit(Answer.Error(it))
        }
        .flowOn(dispatchers.io)
        .distinctUntilChanged()
}