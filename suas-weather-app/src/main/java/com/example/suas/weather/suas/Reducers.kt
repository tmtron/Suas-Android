package com.example.suas.weather.suas

import com.zendesk.suas.Action
import com.zendesk.suas.Reducer

interface Reducers {

    class SuggestedLocationsReducer : Reducer<StateModels.FoundLocations>() {

        override fun reduce(oldState: StateModels.FoundLocations, action: Action<*>): StateModels.FoundLocations {
            return when(action) {
                is LoadSuggestedCities -> {
                    oldState.copy(query = action.query)
                }

                is SuggestedCitiesLoaded -> {
                    val locations = action.result.map { StateModels.Location(it.name, it.zmw) }
                    oldState.copy(foundLocation = locations)
                }

                is SuggestedCitiesError -> {
                    StateModels.FoundLocations()
                }

                else -> {
                    oldState
                }
            }
        }

        override fun getEmptyState(): StateModels.FoundLocations = StateModels.FoundLocations()

    }

    class ProgressReducer : Reducer<StateModels.Progress>() {

        private val decrement = listOf(
                SuggestedCitiesLoaded::class, SuggestedCitiesError::class,
                LoadWeatherSuccess::class, LoadWeatherError::class
        )

        private val increment = listOf(
                LoadSuggestedCities::class, LoadWeather::class
        )

        override fun reduce(oldState: StateModels.Progress, action: Action<*>): StateModels.Progress {
            return if(increment.contains(action::class)) {
                oldState.copy(count = oldState.count + 1)
            } else if(decrement.contains(action::class)) {
                oldState.copy(count = oldState.count - 1)
            } else {
                oldState
            }
        }

        override fun getEmptyState(): StateModels.Progress = StateModels.Progress()
    }

    class LocationsReducer : Reducer<StateModels.Locations>() {

        override fun reduce(oldState: StateModels.Locations, action: Action<*>): StateModels.Locations {
            return when(action) {
                is AddLocation -> {
                    oldState.copy(locations = oldState.locations + listOf(action.location))
                }
                is LocationsLoaded -> {
                    action.location
                }
                else -> {
                    oldState
                }
            }
        }

        override fun getEmptyState(): StateModels.Locations = StateModels.Locations()

    }

    class SelectedLocationReducer : Reducer<StateModels.SelectedLocation>() {

        override fun reduce(oldState: StateModels.SelectedLocation, action: Action<*>): StateModels.SelectedLocation {
            return when(action) {
                is LocationSelected -> {
                    oldState.copy(location = action.location)
                }
                else -> {
                    oldState
                }
            }
        }

        override fun getEmptyState(): StateModels.SelectedLocation = StateModels.SelectedLocation()

    }

    class LoadedObservationsReducer : Reducer<StateModels.LoadedObservations>() {
        override fun reduce(oldState: StateModels.LoadedObservations, action: Action<*>): StateModels.LoadedObservations {
            return when(action) {
                is LoadWeatherSuccess -> {
                    oldState.copy(data = oldState.data + (action.location to action.observation))
                }

                else -> {
                    oldState
                }
            }
        }

        override fun getEmptyState(): StateModels.LoadedObservations = StateModels.LoadedObservations()

    }

}