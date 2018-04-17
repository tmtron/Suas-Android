package com.example.suas.weather.network

import com.google.gson.annotations.SerializedName

interface NetworkModels {

    data class AutocompleteResult(
            @SerializedName("RESULTS") val items: List<AutocompleteItem>)
    
    data class AutocompleteItem(
            val name: String,
            val l: String,
            val zmw: String,
            val lat: Float,
            val lon: Float)

    data class ConditionResponse(
            @SerializedName("current_observation") val currentObservation: Observation)

    data class Observation(
        @SerializedName("temp_c") val temp: Float,
        @SerializedName("icon_url") val iconUrl: String,
        @SerializedName("display_location") val location: Location,
        @SerializedName("weather") val weather: String,
        @SerializedName("precip_today_string") val precip: String,
        @SerializedName("wind_string") val wind: String
    )

    data class Location(
            val full: String,
            val city: String
    )
}