package com.example.suas.weather.storage

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.example.suas.weather.suas.LocationsLoaded
import com.example.suas.weather.suas.StateModels
import com.google.gson.Gson
import com.zendesk.suas.Action
import com.zendesk.suas.AsyncMiddleware
import com.zendesk.suas.Store

class Storage(context: Context) {

    private val sharedPrefs: SharedPreferences by lazy { context.getSharedPreferences("locations", Context.MODE_PRIVATE) }
    private val gson: Gson by lazy { Gson() }

    private fun store(location: StateModels.Locations) {
        sharedPrefs
                .edit()
                .putString("l", gson.toJson(location))
                .apply()
    }

    private fun load(): StateModels.Locations? {
        val data = sharedPrefs.getString("l", "")
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
                Handler(Looper.getMainLooper()).post({
                    dispatcher.dispatchAction(LocationsLoaded(it))
                })
            }
        }
    }

    fun register(store: Store) {
        store.addListener(
                StateModels.Locations::class.java,
                { oldState, newState -> newState != oldState },
                { _, newState -> store(newState) }
        )
    }
}