package com.example.suas.weather.search

import android.view.View
import android.widget.TextView
import com.example.suas.weather.Subscription
import com.example.suas.weather.suas.StateModels
import com.zendesk.suas.Component
import com.zendesk.suas.Selector
import com.zendesk.suas.State
import com.zendesk.suas.Store

class NoResultsComponent(val view: TextView) : Component<State, Boolean>, Subscription {

    override fun update(e: Boolean) {
        view.visibility = if(e) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun getSelector(): Selector<State, Boolean> = Selector {
        val progress = it.getState(StateModels.Progress::class.java.simpleName) as StateModels.Progress
        val locations = it.getState(StateModels.FoundLocations::class.java.simpleName) as StateModels.FoundLocations
        progress.count == 0 && locations.query.isNotBlank() && locations.foundLocation.isEmpty()
    }

    override fun disconnect(store: Store) {
        store.disconnect(this)
    }

    override fun connect(store: Store) {
        store.connect(this)
    }

}