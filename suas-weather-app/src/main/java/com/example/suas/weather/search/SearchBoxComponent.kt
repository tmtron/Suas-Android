package com.example.suas.weather.search;

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.example.suas.weather.Subscription
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.suas.StateModels
import zendesk.suas.*

class SearchBoxComponent(val inputBox: EditText, store: StoreApi, service: WeatherService) :
        Component<StateModels.Progress, Boolean>, Subscription {

    init {
        inputBox.setOnEditorActionListener{ _, keyCode, _ ->
            if(keyCode == EditorInfo.IME_ACTION_DONE) {
                if(inputBox.text.isNotBlank()) {
                    store.dispatchAction(service.findCities(inputBox.text.toString()))
                }
            }
            false
        }
    }

    override fun update(e: Boolean) {
        inputBox.isEnabled = !e
    }

    override fun getSelector(): Selector<StateModels.Progress, Boolean> = Selector { (count) -> count > 0 }

    override fun disconnect(store: Store) {
        store.disconnect(this)
    }

    override fun connect(store: Store) {
        store.connect(this, StateModels.Progress::class.java)
    }
}