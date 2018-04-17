package com.example.suas.weather.network

import com.example.suas.weather.suas.*
import zendesk.suas.Action
import zendesk.suas.AsyncMiddleware
import retrofit2.Call
import retrofit2.Response

class WeatherService(val autocomplete: AutocompleteService, val weather: WundergroundService) {

    fun findCities(query: String): Action<*> {
        return AsyncMiddleware.create { dispatcher, _ ->
            dispatcher.dispatch(LoadSuggestedCities(query))
            loadCitiesFromNetwork(query){
                when(it) {
                    is NetworkError -> dispatcher.dispatch(SuggestedCitiesError())
                    is SuggestionResult -> dispatcher.dispatch(SuggestedCitiesLoaded(it.result))
                }
            }
        }
    }

    fun loadWeather(location: Location): Action<*> {
        return AsyncMiddleware.create { dispatcher, getState ->
            dispatcher.dispatch(LoadWeather(location))

            val observations = getState.state.getState(StateModels.LoadedObservations::class.java)
            val observation = observations?.data?.get(location)

            if(observation != null) {
                // return the cached observation
                dispatcher.dispatch(LoadWeatherSuccess(observation, location))
            } else {
                // start a network-call
                loadWeatherFromNetwork(location) {
                    when (it) {
                        is NetworkError -> dispatcher.dispatch(LoadWeatherError())
                        is ConditionResult -> dispatcher.dispatch(LoadWeatherSuccess(it.result, location))
                    }
                }
            }
        }
    }

    // networkCallback will be executed when we get back a result: NetworkSuccess or Error
    private fun loadCitiesFromNetwork(query: String, networkCallback: NetworkCallback) {
        autocomplete
                // execute the retrofit call
                .autocomplete(query = query)
                .bridge(networkCallback){ (/* AutocompleteResult.*/ items) -> SuggestionResult(items) }
    }

    private fun loadWeatherFromNetwork(location: Location, networkCallback : NetworkCallback) {
        weather
                .getConditions(id = location.id)
                .bridge(networkCallback) { (/* ConditionResponse.*/currentObservation) -> ConditionResult(currentObservation) }
    }

    // bridge the retrofit NetworkCallback to our NetworkResult classes
    // convert will only be used in the success case to map from the AutocompleteResult to our NetworkResult classes
    //         e.g. AutocompleteResult to SuggestionResult
    //         note: both classes contain a list of AutocompleteItem objects: in Clean Architecture we should map
    //               the AutocompleteItem  to a new data-object
    private fun <E, F> Call<E>.bridge(networkCallback: NetworkCallback, convert: (data: E) -> NetworkSuccess<F>) {
        enqueue(object : retrofit2.Callback<E>{

            override fun onResponse(call: Call<E>?, response: Response<E>?) {
                val result = if(response?.isSuccessful ?: false) {
                    response?.body()?.let(convert) ?: NetworkError("Err0r")
                } else {
                    NetworkError(response?.message() ?: "Err0r")
                }

                networkCallback(result)
            }

            override fun onFailure(call: Call<E>?, t: Throwable?) {
                networkCallback(NetworkError(t?.message ?: "Err0r"))
            }

        })
    }

}