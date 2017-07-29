package com.example.suas.weather.search

import android.view.View
import android.widget.TextView
import com.example.suas.weather.Subscription
import com.example.suas.weather.suas.StateModels
import zendesk.suas.Component
import zendesk.suas.Selector
import zendesk.suas.State
import zendesk.suas.Store

class NoResultsComponent(val view: TextView) : Component<State, Boolean>, Subscription {

    override fun update(e: Boolean) {
        view.visibility = if(e) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun getSelector(): Selector<State, Boolean> = Selector {
        val progress = it.getState(StateModels.Progress::class.java)
        val locations = it.getState(StateModels.FoundLocations::class.java)

        if(progress != null && locations != null) {
            progress.count == 0 && locations.query.isNotBlank() && locations.foundLocation.isEmpty()
        } else {
            false
        }
    }

    override fun disconnect(store: Store) {
        store.disconnect(this)
    }

    override fun connect(store: Store) {
        store.connect(this)
    }

}