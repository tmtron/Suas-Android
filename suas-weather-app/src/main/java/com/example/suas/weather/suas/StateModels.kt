package com.example.suas.weather.suas

import com.example.suas.weather.network.NetworkModels

interface StateModels {

    // for auto-completion
    data class FoundLocations(val query: String = "", val foundLocations: List<Location> = listOf())

    data class Progress(val count: Int = 0)

    // the locations that the user has loaded - will be read from/written to shared preferences
    data class Locations(val locations: List<Location> = listOf())

    // currently active location
    data class SelectedLocation(val location: Location? = null)

    // the loaded weather data for the locations
    data class LoadedObservations(val data: Map<Location, NetworkModels.Observation> = mapOf())

}