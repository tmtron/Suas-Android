package com.example.suas.weather.suas

import com.example.suas.weather.network.NetworkModels
import com.example.suas.weather.network.NetworkModels.AutocompleteItem
import com.example.suas.weather.suas.StateModels.Location
import com.example.suas.weather.suas.StateModels.Locations
import zendesk.suas.Action


object ActionTypes {
    const val loadSuggestions = "LOAD_CITIES"
    const val suggestionsLoaded = "CITIES_LOADED_SUCCESS"
    const val suggestionsError= "CITIES_LOADED_ERROR"


    const val loadWeather = "LOAD_WEATHER"
    const val loadWeatherError = "LOAD_WEATHER_ERROR"
    const val loadWeatherSuccess = "LOAD_WEATHER_SUCCESS"

    const val addLocation = "ADD_LOCATION"
    const val locationsLoadedFromDisk = "LOCATIONS_LOADED_FROM_DISK"
    const val selectLocation = "SELECT_LOCATION"
}

class LoadSuggestedCities(val query: String): Action<String>(ActionTypes.loadSuggestions, query)

class SuggestedCitiesLoaded(val result: List<AutocompleteItem>): Action<List<AutocompleteItem>>(ActionTypes.suggestionsLoaded, result)

class SuggestedCitiesError: Action<String>(ActionTypes.suggestionsError)


class AddLocation(val location: Location): Action<Location>(ActionTypes.addLocation, location)

class LocationsLoaded(val location: Locations): Action<Locations>(ActionTypes.locationsLoadedFromDisk, location)

class LocationSelected(val location: Location): Action<Location>(ActionTypes.selectLocation, location)


class LoadWeather(val location: Location): Action<Location>(ActionTypes.loadWeather, location)

class LoadWeatherError: Action<String>(ActionTypes.loadWeatherError)

class LoadWeatherSuccess(val observation: NetworkModels.Observation, val location: Location): Action<Pair<NetworkModels.Observation, Location>>(ActionTypes.loadWeatherSuccess, Pair(observation, location))