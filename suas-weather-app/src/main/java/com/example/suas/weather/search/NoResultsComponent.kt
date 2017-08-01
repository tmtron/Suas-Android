package com.example.suas.weather.search

import android.view.View
import android.widget.TextView
import com.example.suas.weather.suas.StateModels
import zendesk.suas.Listener
import zendesk.suas.StateSelector

class NoResultsComponent(val view: TextView) : Listener<Boolean> {

    override fun update(e: Boolean) {
        view.visibility = if(e) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    val selector = StateSelector {
        val progress = it.getState(StateModels.Progress::class.java)
        val locations = it.getState(StateModels.FoundLocations::class.java)

        if (progress != null && locations != null) {
            progress.count == 0 && locations.query.isNotBlank() && locations.foundLocation.isEmpty()
        } else {
            false
        }
    }

}