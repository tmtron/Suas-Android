package com.example.suas.weather

import android.app.Activity
import android.app.Application
import com.example.suas.weather.network.AutocompleteService
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.network.WundergroundService
import com.example.suas.weather.storage.Storage
import com.example.suas.weather.suas.Reducers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zendesk.suas.*
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

    private val storage: Storage by lazy { Storage(this) }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().apply {
            baseUrl("http://api.wunderground.com/api/").build()
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    val weatherService: WeatherService by lazy {
        val autocompleteService = retrofit.create(AutocompleteService::class.java)
        val wundergroundService = retrofit.create(WundergroundService::class.java)
        WeatherService(autocomplete = autocompleteService, weather = wundergroundService)
    }

    val store : Store by lazy {

        val logger = LoggerMiddleware.Builder()
                .withSerialization(LoggerMiddleware.Serialization.GSON)
                .withLineLength(-1)
                .build()

        val monitor = MonitorMiddleware.Builder(this)
                .withEnableAdb(true)
                .withEnableBonjour(true)
                .build()

        val reducers = listOf(
                Reducers.SuggestedLocationsReducer(),
                Reducers.ProgressReducer(),
                Reducers.LocationsReducer(),
                Reducers.SelectedLocationReducer(),
                Reducers.LoadedObservationsReducer()
        )

        val store = Suas.createStore(reducers)
                .withMiddleware(AsyncMiddleware(), monitor, logger)
                .withDefaultFilter(Filters.EQUALS)
                .build()

        // load the existing location list from the shared preferences
        store.dispatch(storage.loadAction())
        // register a listener that will save location changes to the shared preferences
        storage.register(store)

        store
    }

}