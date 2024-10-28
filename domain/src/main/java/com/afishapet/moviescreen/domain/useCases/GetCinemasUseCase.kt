package com.afishapet.moviescreen.domain.useCases

import com.afishapet.moviescreen.domain.models.Answer
import com.afishapet.moviescreen.domain.models.Cinema
import com.afishapet.moviescreen.domain.repository.CinemasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetCinemasUseCase @Inject constructor(
    private val cinemasRepository: CinemasRepository
) {
    operator fun invoke(): Flow<Answer<List<Cinema>>> = cinemasRepository.getCinemas()
        .catch {
            emit(Answer.Error(it))
        }
        .distinctUntilChanged()

}
