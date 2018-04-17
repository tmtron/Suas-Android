package com.example.suas.weather.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.suas.weather.suas.LocationsLoaded
import com.example.suas.weather.suas.StateModels
import com.google.gson.Gson
import zendesk.suas.Action
import zendesk.suas.AsyncMiddleware
import zendesk.suas.Store

private const val SHARED_PREFS_KEY = "1"

class Storage(context: Context) {

    private val sharedPrefs: SharedPreferences by lazy { context.getSharedPreferences("locations", Context.MODE_PRIVATE) }
    private val gson: Gson by lazy { Gson() }

    private fun store(locations: StateModels.Locations) {
        sharedPrefs
                .edit()
                .putString(SHARED_PREFS_KEY, gson.toJson(locations))
                .apply()
    }

    private fun load(): StateModels.Locations? {
        val data = sharedPrefs.getString(SHARED_PREFS_KEY, "")
        return try {
            gson.fromJson(data, StateModels.Locations::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun loadAction(): Action<*> {
        return AsyncMiddleware.forBlockingAction{ dispatcher, _ ->
            val data = load()
            data?.let {
                dispatcher.dispatch(LocationsLoaded(it))
            }
        }
    }

    fun register(store: Store) {
        store.addListener(
                StateModels.Locations::class.java,
                { oldState, newState -> newState != oldState },
                { store(it) }
        )
    }
}