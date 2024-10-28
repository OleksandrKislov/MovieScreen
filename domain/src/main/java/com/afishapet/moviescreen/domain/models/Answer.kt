package com.afishapet.moviescreen.domain.models

sealed class Answer <out T> {
    data class Success<out T>(val data: T) : Answer<T>()
    data class Error(val exception: Throwable) : Answer<Nothing>()
    data object Loading : Answer<Nothing>()
}