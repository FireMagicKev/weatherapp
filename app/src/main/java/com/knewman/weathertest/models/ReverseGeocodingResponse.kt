package com.knewman.weathertest.models
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ReverseGeocodingResponse(
    @SerialName("country")
    val country: String?,
    @SerialName("lat")
    val lat: Double,
    @SerialName("local_names")
    val localNames: LocalNames,
    @SerialName("lon")
    val lon: Double,
    @SerialName("name")
    val name: String?,
    @SerialName("state")
    val state: String?
) {
    val searchQuery: String
        get() = listOf(name, state, country).joinToString()
}

@Serializable
data class LocalNames(
    @SerialName("en")
    val en: String,
)