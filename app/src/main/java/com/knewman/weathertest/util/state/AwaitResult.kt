package com.knewman.weathertest.util.state

import okhttp3.Response

sealed interface AwaitResult<out R> {
    data class Ok<out T>(val value: T, val response: Response): AwaitResult<T>
    data class Error(val exception: Exception,
                     val response: Response? = null,
                     val jsonError: String? = null,
                     val errorResponse: ErrorResponse? = null): AwaitResult<Nothing>
}

fun <T> AwaitResult<T>.toState(): State<T> {
    return when (this) {
        is AwaitResult.Ok -> {
            success { value }
        }
        is AwaitResult.Error -> {
            error { exception }
        }
    }
}

data class ErrorResponse(val message: String?,
                         val stacktrace: String?)