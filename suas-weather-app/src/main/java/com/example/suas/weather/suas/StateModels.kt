package com.example.suas.weather.suas

import com.example.suas.weather.network.NetworkModels

interface StateModels {

    data class FoundLocations(val query: String = "", val foundLocations: List<Location> = listOf())

    data class Progress(val count: Int = 0)

    data class Locations(val locations: List<Location> = listOf())

    data class SelectedLocation(val location: Location? = null)

    data class LoadedObservations(val data: Map<Location, NetworkModels.Observation> = mapOf())

}