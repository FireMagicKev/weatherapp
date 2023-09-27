package com.knewman.weathertest.util.extensions

import com.knewman.weathertest.util.state.AwaitResult
import com.knewman.weathertest.util.state.ErrorResponse
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.awaitResult(): AwaitResult<T> {
    return try {
        if (isSuccessful) {
            val body = body()
            if (body != null) {
                AwaitResult.Ok(body, raw())
            } else {
                AwaitResult.Error(NullPointerException("Response body is null"), raw(), errorBody()?.string())
            }
        } else {
            AwaitResult.Error(HttpException(this), raw(), errorBody()?.string())
        }
    } catch (e: Exception) {
        val json = errorBody()?.string()
        val errorResponse = if (json != null) {
            // Moshi.Builder().build().adapter(ErrorResponse::class.java).fromJson(json)
            Json.decodeFromString<ErrorResponse>(json)
        } else null

        AwaitResult.Error(e, raw(), errorBody()?.string(), errorResponse)
    }
}

val <T> Response<T>.value: T
    get() = try {
        body() as T
    } catch (e: Exception) {
        throw HttpException(this)
    }