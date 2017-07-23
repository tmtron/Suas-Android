package com.example.suas.weather.locationList

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.suas.weather.Subscription
import com.example.suas.weather.suas.StateModels
import com.squareup.picasso.Picasso
import com.zendesk.suas.Component
import com.zendesk.suas.Selector
import com.zendesk.suas.State
import com.zendesk.suas.Store

class WeatherComponent(val imageView: ImageView,
                       val location: TextView,
                       val weather: TextView,
                       val precip: TextView,
                       val wind: TextView
) : Component<State, WeatherComponent.Weather>, Subscription {


    private val views = listOf(imageView, location, precip, wind, weather)

    override fun update(e: WeatherComponent.Weather) {
        when(e) {
            is Weather.Forecast -> {
                Picasso.with(imageView.context).load(e.forecastIcon).into(imageView)
                location.text = "${e.temperature}°C in ${e.name}"
                weather.text = e.weather
                precip.text = "Precip: ${e.precip}"
                wind.text= "Wind: ${e.wind}"
                views.forEach { it.visibility = View.VISIBLE }
            }
            is Weather.NoForecast -> {
                views.forEach { it.visibility = View.INVISIBLE }
            }
        }
    }

    override fun getSelector(): Selector<State, WeatherComponent.Weather> = Selector { state ->
        val selectedLocation = state.getState(StateModels.SelectedLocation::class.java)
        val loadedLocation = state.getState(StateModels.LoadedObservations::class.java)

        val observation = selectedLocation?.location?.let { loadedLocation?.data?.get(it) }
        if (observation != null) {
            Weather.Forecast(observation.location.full, observation.temp, observation.iconUrl, observation.weather, observation.wind, observation.precip)
        } else {
            Weather.NoForecast
        }
    }

    override fun disconnect(store: Store) {
        store.disconnect(this)
    }

    override fun connect(store: Store) {
        store.connect(this)
    }

    sealed class Weather {
        data class Forecast(val name: String, val temperature: Float, val forecastIcon: String, val weather: String, val wind: String, val precip: String) : Weather()
        object NoForecast : Weather()
    }
}