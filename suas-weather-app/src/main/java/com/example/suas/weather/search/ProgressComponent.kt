package com.example.suas.weather.search;

import android.view.View
import android.widget.ProgressBar
import com.example.suas.weather.suas.StateModels
import zendesk.suas.Listener

class ProgressComponent(val progressBar: ProgressBar) : Listener<StateModels.Progress> {

    override fun update(e: StateModels.Progress) {
        progressBar.visibility = if(e.count > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}