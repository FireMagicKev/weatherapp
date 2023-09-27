package com.knewman.weathertest.util.state

sealed interface State<out T> {
    data object Loading: State<Nothing>
    data class Success<T>(val value: T): State<T>
    data class Error(val exception: Exception): State<Nothing>
}

fun <T> success(block: () -> T): State.Success<T> {
    return State.Success(block())
}

fun <T : Exception> error(block: () -> T): State.Error {
    return State.Error(block())
}