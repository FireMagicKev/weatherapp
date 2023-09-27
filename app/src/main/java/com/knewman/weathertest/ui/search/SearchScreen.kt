package com.knewman.weathertest.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.knewman.weathertest.models.CurrentWeatherResponse
import com.knewman.weathertest.util.extensions.tryOrNull
import com.knewman.weathertest.util.state.State

//A callback for receiving notifications from the FusedLocationProviderClient.
lateinit var locationCallback: LocationCallback

//The main entry point for interacting with the Fused Location Provider.
lateinit var locationProvider: FusedLocationProviderClient

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen() {
    val context = LocalContext.current
    locationProvider = LocationServices.getFusedLocationProviderClient(context)

    val viewModel = hiltViewModel<SearchViewModel>()
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    if (locationPermissionState.allPermissionsGranted) {
        getUserLocation(context)?.let {
            LaunchedEffect(Unit) {
                viewModel.fetchWeatherResultsByCoord(it.latitude, it.longitude)
            }
        }
        /*  Ideally, we'd show an empty state explaining to the user that location permissions
        is required. But in the sake of time, this step has been skipped
    */
    } else {
        LaunchedEffect(Unit) {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        val lastSavedLocation = viewModel.getLastSavedLocationEntered()
        if (lastSavedLocation?.isNotBlank() == true) {
            viewModel.searchInput = lastSavedLocation
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.searchInput, onValueChange = {
                viewModel.searchInput = it
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.fetchWeatherResultsByCityInput()
                }
            )
        )

        when (val results = viewModel.weatherResults.collectAsStateWithLifecycle().value) {
            State.Loading -> {
                // This would be the initial loading screen showing while fetching data from the server
                Text(text = "Loading")
            }

            is State.Error -> {
                // This would be the screen shown if there's an error returned from the server
                Text(text = "${results.exception}")
            }

            is State.Success -> {
                viewModel.saveLastLocationEntered()
                SearchResultsSuccess(results = results.value)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun getUserLocation(context: Context): Location? {
    locationProvider = LocationServices.getFusedLocationProviderClient(context)
    var currentUserLocation: Location? by remember { mutableStateOf(null) }

    DisposableEffect(key1 = locationProvider) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                currentUserLocation = result.locations.firstOrNull()
            }
        }

        startLocationUpdate()

        onDispose {
            stopLocationUpdate()
        }
    }
    //
    return currentUserLocation
}

@SuppressLint("MissingPermission")
fun startLocationUpdate() {
    locationCallback.let {
        //An encapsulation of various parameters for requesting
        // location through FusedLocationProviderClient.
        val locationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
        //use FusedLocationProviderClient to request location update
        locationProvider.requestLocationUpdates(
            locationRequest,
            it,
            null,
        )
    }
}

fun stopLocationUpdate() {
    tryOrNull { locationProvider.removeLocationUpdates(locationCallback) }
}

@Composable
fun SearchResultsSuccess(results: CurrentWeatherResponse) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = results.weather?.firstOrNull()?.iconUrl,
                contentDescription = null,
            )

            Text(text = results.name ?: "")
        }
        Text(text = "Current temp: ${results.main?.temp}")
        Text(text = "Feels like: ${results.main?.feelsLike}")
        LazyColumn {
            items(results.weather ?: emptyList()) { weather ->
                Text(text = "Conditions: ${weather.main}. ${weather.description}")
            }
        }
    }
}