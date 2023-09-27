package com.knewman.weathertest.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knewman.weathertest.models.CurrentWeatherResponse
import com.knewman.weathertest.network.CurrentWeatherManager
import com.knewman.weathertest.util.PreferencesManager
import com.knewman.weathertest.util.state.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val manager: CurrentWeatherManager,
    private val preferencesManager: PreferencesManager,
): ViewModel() {
    var searchInput by mutableStateOf("")

    private val _weatherResults: MutableStateFlow<State<CurrentWeatherResponse>> = MutableStateFlow(State.Loading)
    val weatherResults = _weatherResults.asStateFlow()

    fun fetchWeatherResultsByCityInput() {
        viewModelScope.launch {
            _weatherResults.value = manager.fetchWeatherByCity(searchInput)
        }
    }

    fun fetchWeatherResultsByCoord(lat: Double, lng: Double) {
        viewModelScope.launch {
            searchInput = when (val cityResult = manager.fetchWeatherByCoordinate(lat, lng)) {
                is State.Success -> {
                    cityResult.value.firstOrNull()?.searchQuery ?: ""
                }
                else -> ""
            }

            _weatherResults.value = manager.fetchWeatherByCity(searchInput)
        }
    }

    fun saveLastLocationEntered() {
        preferencesManager["city"] = searchInput
    }

    fun getLastSavedLocationEntered(): String? {
        return preferencesManager["city"]
    }
}