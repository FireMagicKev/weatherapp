package com.knewman.weathertest.network

import com.knewman.weathertest.models.CurrentWeatherResponse
import com.knewman.weathertest.models.ReverseGeocodingResponse
import com.knewman.weathertest.util.extensions.awaitResult
import com.knewman.weathertest.util.state.State
import com.knewman.weathertest.util.state.toState
import javax.inject.Inject

class CurrentWeatherManager @Inject constructor(private val service: WeatherService) {
    suspend fun fetchWeatherByCity(query: String): State<CurrentWeatherResponse> {
        return service.fetchWeatherByCity(query).awaitResult().toState()
    }

    suspend fun fetchWeatherByCoordinate(lat: Double, lng: Double): State<List<ReverseGeocodingResponse>> {
        return service.reverseGeocoding(lat, lng).awaitResult().toState()
    }
}