package com.knewman.weathertest.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.knewman.weathertest.network.CurrentWeatherManager
import com.knewman.weathertest.network.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesJson() = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun providesRetrofit(json: Json): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val url = chain
                    .request()
                    .url
                    .newBuilder()
                    .addQueryParameter("appid", "687a32b7a0814a259cf6402333f12481")
                    .build()
                chain.proceed(chain.request().newBuilder().url(url).build())
            }
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun providesBusinessSearchService(retrofit: Retrofit): WeatherService = retrofit.create(
        WeatherService::class.java
    )

    @Provides
    @Singleton
    fun providesBusinessSearchManager(service: WeatherService) = CurrentWeatherManager(
        service
    )
}