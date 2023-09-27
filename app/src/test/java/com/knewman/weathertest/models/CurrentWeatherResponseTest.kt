package com.knewman.weathertest.models

import junit.framework.TestCase
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CurrentWeatherResponseTest : TestCase() {
    private var currentWeatherResponse: CurrentWeatherResponse? = null

    @Before
    public override fun setUp() {
        super.setUp()
        currentWeatherResponse = try {
            val stream = javaClass.getResourceAsStream("/current_weather_response_success.json") ?: return
            Json.decodeFromStream<CurrentWeatherResponse>(stream)
        } catch (e: Exception) {
            null
        }
    }

    @Test
    fun shouldReturnBaseAsStations() {
        assert(currentWeatherResponse?.base == "stations")
    }

    @Test
    fun shouldHaveNonEmptyWeatherList() {
        assert(currentWeatherResponse?.weather?.isNotEmpty() == true)
    }
}