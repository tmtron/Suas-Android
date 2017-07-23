package com.example.suas.weather

import android.app.Activity
import android.app.Application
import com.example.suas.weather.async.AsyncMiddleware
import com.example.suas.weather.network.AutocompleteService
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.network.WundergroundService
import com.example.suas.weather.storage.Storage
import com.example.suas.weather.suas.Reducers
import com.example.suas.weather.suas.StateModels
import com.zendesk.suas.LoggerMiddleware
import com.zendesk.suas.ReduxStore
import com.zendesk.suas.State
import com.zendesk.suas.Store
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.RuntimeException

class WeatherApplication : Application() {

    companion object {
        fun from(activity: Activity): WeatherApplication {
            if(activity.application is WeatherApplication) {
                return activity.application as WeatherApplication
            } else {
                throw RuntimeException("No weather app :(")
            }
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().apply {
            baseUrl("http://api.wunderground.com/api/").build()
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    private val autocompleteService: AutocompleteService by lazy { retrofit.create(AutocompleteService::class.java) }
    private val wundergroundService: WundergroundService by lazy { retrofit.create(WundergroundService::class.java) }
    private val storage: Storage by lazy { Storage(this) }

    val weatherService: WeatherService by lazy { WeatherService(autocomplete = autocompleteService, weather = wundergroundService) }
    val store : Store by lazy {

        val logger = LoggerMiddleware.Builder()
                .setSerialization(LoggerMiddleware.Serialization.GSON)
                .setLineLength(-1)
                .build()

        val reducers = listOf(
                Reducers.SuggestedLocationsReducer(),
                Reducers.ProgressReducer(),
                Reducers.LocationsReducer(),
                Reducers.SelectedLocationReducer(),
                Reducers.LoadedObservationsReducer()
        )

        val locationState = storage.load()
        val state = if(locationState != null) {
            val map = reducers.map { it.emptyState.javaClass.simpleName to it.emptyState }.toMap()
            State(map.plus(StateModels.Locations::class.java.simpleName to locationState))
        } else {
            null
        }

        val store = ReduxStore.Builder(reducers).apply {
                withMiddleware(AsyncMiddleware(), logger)
                if(state != null) {
                    withInitialState(state)
                }
        }.build()

        storage.register(store)
        store
    }

}