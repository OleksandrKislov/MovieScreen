package com.afishapet.moviescreen.data.datasource

import com.afishapet.moviescreen.domain.models.Answer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext

fun <T> requestAnswer(
    context: CoroutineContext,
    block: suspend () -> T
) = flow<Answer<T>> {
    emit(Answer.Success(block()))
}.onStart {
    emit(Answer.Loading)
}.flowOn(context)