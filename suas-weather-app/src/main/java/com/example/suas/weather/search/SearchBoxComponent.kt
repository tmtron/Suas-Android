package com.example.suas.weather.search;

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.suas.StateModels
import zendesk.suas.Listener
import zendesk.suas.Dispatcher


class SearchBoxComponent(val inputBox: EditText, dispatcher: Dispatcher, service: WeatherService) : Listener<StateModels.Progress> {

    init {
        inputBox.setOnEditorActionListener{ _, keyCode, _ ->
            if(keyCode == EditorInfo.IME_ACTION_DONE) {
                if(inputBox.text.isNotBlank()) {
                    dispatcher.dispatch(service.findCities(inputBox.text.toString()))
                }
            }
            false
        }
    }

    override fun update(e: StateModels.Progress) {
        inputBox.isEnabled = e.count == 0
    }

}