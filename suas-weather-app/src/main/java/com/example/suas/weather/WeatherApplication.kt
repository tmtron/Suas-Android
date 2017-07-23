package com.example.suas.weather

import android.app.Activity
import android.app.Application
import com.example.suas.weather.network.AutocompleteService
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.network.WundergroundService
import com.example.suas.weather.storage.Storage
import com.example.suas.weather.suas.Reducers
import com.zendesk.suas.*
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

        val monitor = MonitorMiddleware.Builder(this)
                .setEnableAdb(true)
                .setEnableBonjour(true)
                .build()

        val reducers = listOf(
                Reducers.SuggestedLocationsReducer(),
                Reducers.ProgressReducer(),
                Reducers.LocationsReducer(),
                Reducers.SelectedLocationReducer(),
                Reducers.LoadedObservationsReducer()
        )

        val store = ReduxStore.Builder(reducers)
                .withMiddleware(AsyncMiddleware(), monitor, logger)
                .build()

        store.dispatchAction(storage.loadAction())
        storage.register(store)

        store
    }

}